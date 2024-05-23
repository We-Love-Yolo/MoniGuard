using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Caching.Distributed;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.Caching.StackExchangeRedis;
using System.Security.Claims;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Identity.Web.Resource;
using MoniGuardAPI.Data;
using System.Text;
using System.Text.Json;
using System.Buffers.Text;
using Newtonsoft.Json.Linq;
using System.Linq;

namespace MoniGuardAPI.Controllers
{


    //this controller is used to test the redis cache
    [Authorize]
    [ApiController]
    [Route("[controller]/[action]")]
    public class AnalysisController(IDistributedCache distributedCache, MoniGuardAPIContext context) : ControllerBase
    {
        private readonly IDistributedCache _distributedCache = distributedCache;

        [HttpPost]
        public async Task<IActionResult> UpdateMessages(Message message)
        {
            var authorizedResident = await GetAuthorizedResident();
            if (authorizedResident == null)
            {
                return NotFound();
            }
            var id = authorizedResident.ResidentId;
            byte[]? messagesStr = await _distributedCache.GetAsync($"{id}_test");
            var cacheEntryOptions = new DistributedCacheEntryOptions()
                .SetSlidingExpiration(TimeSpan.FromDays(2));
            if (messagesStr == null)
            {
                List<Message> messages = [message];
                var json = JsonSerializer.Serialize(messages);
                await _distributedCache.SetStringAsync($"{id}_test", json, cacheEntryOptions);
            }
            else
            {
                var messages = JsonSerializer.Deserialize<List<Message>>(Encoding.UTF8.GetString(messagesStr))!;
                messages.Add(message);
                var json = JsonSerializer.Serialize(messages);
                await _distributedCache.SetStringAsync($"{id}_test", json, cacheEntryOptions);
            }
            //todo: add the message to the database
            return Ok();
        }


        [HttpGet]
        public async Task<ActionResult<List<Message>>> GetMessages()
        {
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return NotFound();
            }
            var id = resident.ResidentId;
            byte[]? messagesStr = await _distributedCache.GetAsync($"{id}_test");
            List<Message> res;
            if (messagesStr == null)
            {
                res = new List<Message>();
                return Ok(res);
            }
            else
            {
                res = JsonSerializer.Deserialize<List<Message>>(Encoding.UTF8.GetString(messagesStr));
                await _distributedCache.RemoveAsync($"{id}_test");
                return Ok(res);
            }
           
        }

        [HttpGet("{sceneId:int}")]
        public async Task<ActionResult<List<Face>>> GetFaces(int sceneId)
        {
            if (sceneId <= 0)
            {
                return BadRequest();
            }

            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return BadRequest();
            }

            // 使用 Join 操作连接 Scene、Guests 和 Faces 表
            var faces = await (from s in context.Scene
                               join g in context.Guests on s.SceneId equals g.SceneId
                               join f in context.Faces on g.GuestId equals f.GuestId
                               where s.SceneId == sceneId && s.ResidentId == resident.ResidentId
                               select new Face(f.FaceId, f.GuestId, f.Name, f.CapturedAt, f.Hash)).ToListAsync();

            return faces;
        }

        [HttpGet("{faceId:int}")]
        public async Task<IActionResult> GetFaceImage(int faceId)
        {
            if (faceId <= 0)
            {
                return BadRequest();
            }
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return BadRequest();
            }
            var faceImage = await context.Faces.FirstAsync<Face>(f => f.FaceId == faceId);
            return File(faceImage.Content, "image/*");
        }

        [HttpPost("{sceneId:int}")]
        public async Task<ActionResult<Face>> PostFace(int sceneId, [FromBody] Face face)
        {
            if (sceneId <= 0)
            {
                return BadRequest();
            }
            var scene = await context.Scene.FirstAsync(s => s.SceneId == sceneId);
            if (scene == null)
            {
                return NotFound();
            }
            var resident = await GetAuthorizedResident();
            if (resident==null)
            {
                return Unauthorized();
            }
            if (resident.ResidentId != scene.ResidentId)
            {
                return BadRequest();
            }
            var guest = await context.Guests.FirstAsync(g => g.GuestId == face.GuestId);
            if (guest == null)
            {
                guest = new Guest(sceneId);
                await context.Guests.AddAsync(guest);
                await context.SaveChangesAsync();
            }
            if (guest.SceneId != sceneId) 
            {
                return BadRequest();
            }

            await context.Faces.AddAsync(face);
            await context.SaveChangesAsync();
            return face;
        }

        [HttpPut("{faceId:int}")]
        public async Task<IActionResult> PsotFaceImage(int faceId,[FromBody] IFormFile imageFile)
        {
            if (faceId <= 0)
            {
                return BadRequest();
            }
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return Unauthorized();
            }
            var face = await context.Faces.FirstAsync(f => f.FaceId == faceId);
            if (face == null)
            {
                return NotFound();
            }
            //  check face is resident's
            //  faceId -> face.GuestId -> guest.ScenceId -> scene.ResidentId
            //  check if scene.ResidentId == resident.ResidentId;

            var scene = await (from s in context.Scene
                               join g in context.Guests on s.SceneId equals g.SceneId
                               join f in context.Faces on g.GuestId equals f.GuestId
                               where f.FaceId == faceId
                               select new Scene(s.SceneId,s.Name,s.ResidentId)
                               ).FirstAsync();
            if (scene == null || scene.ResidentId != resident.ResidentId)
            {
                return BadRequest();
            }
            byte[] imageData;
            using (var memoryStream = new MemoryStream())
            {
                await imageFile.CopyToAsync(memoryStream);
                imageData = memoryStream.ToArray();
            }
            face.Content = imageData;
            await context.SaveChangesAsync();
            return NoContent();
        }



        private async Task<Resident?> GetAuthorizedResident()
        {
            var nameIdentifier = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (nameIdentifier == null)
            {
                return null;
            }

            var all = context.Resident.Where(r => r.ResidentId == 1);
            var claims = User.Claims;

            var resident = await context.Resident.FirstOrDefaultAsync(r => r.NameIdentifier == nameIdentifier);
            if (resident != null)
            {
                return await context.Resident.FirstOrDefaultAsync(r => r.NameIdentifier == nameIdentifier);
            }
            var email = User.FindFirstValue(ClaimTypes.Email);
            var nickname = User.FindFirstValue("preferred_username");

            resident = new Resident(nameIdentifier,
                nickname ?? "MoniGuard Resident",
                null,
                null,
                email);
            await context.Resident.AddAsync(resident);
            await context.SaveChangesAsync();
            return resident;

        }
    }
}
