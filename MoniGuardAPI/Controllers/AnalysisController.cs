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



        /// <summary>
        /// 添加消息至消息队列
        /// </summary>
        /// <param name="message"></param>
        /// <returns>200: 添加成功</returns>
        [HttpPost]
        public async Task<IActionResult> UpdateMessages(Message message)
        {
            var authorizedResident = await GetAuthorizedResident();
            if (authorizedResident == null)
            {
                return NotFound();
            }
            var id = authorizedResident.ResidentId;
            byte[]? messagesStr = await _distributedCache.GetAsync($"{id}_message");
            var cacheEntryOptions = new DistributedCacheEntryOptions()
                .SetSlidingExpiration(TimeSpan.FromDays(2));
            if (messagesStr == null)
            {
                List<Message> messages = [message];
                var json = JsonSerializer.Serialize(messages);
                await _distributedCache.SetStringAsync($"{id}_message", json, cacheEntryOptions);
            }
            else
            {
                var messages = JsonSerializer.Deserialize<List<Message>>(Encoding.UTF8.GetString(messagesStr))!;
                messages.Add(message);
                var json = JsonSerializer.Serialize(messages);
                await _distributedCache.SetStringAsync($"{id}_message", json, cacheEntryOptions);
            }
            return Ok();
        }


        /// <summary>
        /// 获取当前消息队列所有消息
        /// </summary>
        /// <returns></returns>
        [HttpGet]
        public async Task<ActionResult<List<Message>>> GetMessages()
        {
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return NotFound();
            }
            var id = resident.ResidentId;
            byte[]? messagesStr = await _distributedCache.GetAsync($"{id}_message");
            List<Message> res;
            if (messagesStr == null)
            {
                res = new List<Message>();
                return Ok(res);
            }
            else
            {
                res = JsonSerializer.Deserialize<List<Message>>(Encoding.UTF8.GetString(messagesStr));
                await _distributedCache.RemoveAsync($"{id}_message");
                return Ok(res);
            }
           
        }

        /// <summary>
        /// 通过sceneId获取此场景下的所有人脸信息
        /// </summary>
        /// <param name="sceneId"></param>
        /// <returns>返回人脸信息列表</returns>
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

        /// <summary>
        /// 通过guestId获取此guest的所有人脸信息
        /// </summary>
        /// <param name="guestId"></param>
        /// <returns>返回人脸信息列表</returns>
        [HttpGet("{guestId:int}")]
        public async Task<ActionResult<List<Face>>> GetFacesByGuestId(int guestId)
        {
            if (guestId <= 0)
            {
                return BadRequest();
            }

            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return BadRequest();
            }

            // 使用 Join 操作连接Guests 和 Faces 表
            var faces = await (
                from g in context.Guests
                join f in context.Faces on g.GuestId equals f.GuestId
                where g.GuestId == guestId
                select new Face(f.FaceId, f.GuestId, f.Name, f.CapturedAt, f.Hash)).ToListAsync();

            return faces;
        }


        /// <summary>
        /// 通过faceId获取人脸图片
        /// </summary>
        /// <param name="faceId"></param>
        /// <returns>一张图片</returns>
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

        /// <summary>
        /// 新建一个人脸信息
        /// </summary>
        /// <param name="sceneId"></param>
        /// <param name="face">只有guestId是需要提供的</param>
        /// <returns>返回人脸信息</returns>
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
                return NotFound();
            }
            if (guest.SceneId != sceneId) 
            {
                return BadRequest();
            }

            await context.Faces.AddAsync(face);
            await context.SaveChangesAsync();
            return face;
        }

        /// <summary>
        /// 上传人脸图片，图片使用base64编码
        /// </summary>
        /// <param name="faceId"></param>
        /// <param name="base64Image">图片的base64信息</param>
        /// <returns>204</returns>
        [HttpPut("{faceId:int}")]
        public async Task<IActionResult> PutFaceImage(int faceId,[FromBody] string base64Image)
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
            var imageData = Convert.FromBase64String(base64Image);
            
            face.Content = imageData;
            await context.SaveChangesAsync();
            return NoContent();
        }


        /// <summary>
        /// 新增一个Guest，需要guestId，将faceEncoding base64编码在body中
        /// </summary>
        /// <param name="sceneId"></param>
        /// <param name="faceEncodingDataBase64"></param>
        /// <returns>status = 200 并返回一个Guest</returns>
        [HttpPost("{sceneId:int}")]
        public async Task<IActionResult> NewAGuest(int sceneId,[FromBody] string faceEncodingDataBase64)
        {
            if (sceneId <= 0)
            {
                return BadRequest();
            }
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return Unauthorized();
            }
            var scene = await context.Scene.FirstAsync(s => s.SceneId == sceneId);
            if (scene == null)
            {
                return NotFound();
            }

            if (scene.ResidentId != resident.ResidentId)
            {
                return BadRequest();
            }
            var data = Convert.FromBase64String(faceEncodingDataBase64);
            var guest = new Guest(sceneId, data);
            await context.Guests.AddAsync(guest);
            await context.SaveChangesAsync();
            return Ok(guest);
        }


        /// <summary>
        /// 获得对应的guest的faceEncoding数据
        /// </summary>
        /// <param name="guestId"></param>
        /// <returns>文件</returns>
        [HttpGet("{guestId:int}")]
        public async Task<ActionResult> GetSampleFaceData(int guestId)
        {
            if (guestId <= 0)
            {
                return BadRequest();
            }
            var guest = context.Guests.FirstOrDefault(g => g.GuestId == guestId);
            if (guest == null)
            {
                return NotFound();
            }
            var scene = await context.Scene.FirstOrDefaultAsync(s => s.SceneId == guest.SceneId);
            if (scene == null)
            {
                return NotFound();
            }
            var resident = await GetAuthorizedResident();
            if (resident == null || resident.ResidentId != scene.ResidentId)
            {
                return Unauthorized();
            }

            var fileName = $"guest{guest.GuestId}_face_data.npy";
            var faceData = guest.FaceEncodingDataBytes;
            return File(faceData, "application/octet-stream", fileName);
        }

        /// <summary>
        /// 建立guest和photo连接
        /// </summary>
        /// <param name="guestId"></param>
        /// <param name="photoId"></param>
        /// <returns></returns>
        [HttpPost]
        public async Task<IActionResult> PostGuestToPhoto(int guestId, int photoId)
        {
            if (guestId <= 0 || photoId <= 0)
            {
                return BadRequest();
            }
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return Unauthorized();
            }
            var guest = await context.Guests.FirstOrDefaultAsync(g => g.GuestId == guestId);
            if (guest == null)
            {
                return NotFound();
            }
            var photo = await context.Photos.FirstOrDefaultAsync(p => p.PhotoId == photoId);
            if (photo == null)
            {
                return NotFound();
            }
            var scene = await context.Scene.FirstOrDefaultAsync(s => s.SceneId == guest.SceneId);
            if (scene == null || scene.ResidentId != resident.ResidentId)
            {
                return BadRequest();
            }
            var guestToPhoto = new GuestToPhotos(guestId, photoId);
            await context.GuestToPhotos.AddAsync(guestToPhoto);
            await context.SaveChangesAsync();
            return NoContent();

        }

        /// <summary>
        /// 获取和指定guestId相关的所有photo
        /// </summary>
        /// <param name="guestId"></param>
        /// <returns></returns>
        [HttpGet("{guestId:int}")]
        public async Task<ActionResult<List<Photo>>> GetPhotos(int guestId)
        {
            if (guestId <= 0)
            {
                return BadRequest();
            }
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return Unauthorized();
            }
            var guest = await context.Guests.FirstOrDefaultAsync(g => g.GuestId == guestId);
            if (guest == null)
            {
                return NotFound();
            }
            var scene = await context.Scene.FirstOrDefaultAsync(s => s.SceneId == guest.SceneId);
            if (scene == null || scene.ResidentId != resident.ResidentId)
            {
                return BadRequest();
            }
            var photos = await (from p in context.Photos
                join g in context.GuestToPhotos on p.PhotoId equals g.PhotoId
                where g.GuestId == guestId
                select p).ToListAsync();
            return photos;
        }


        /// <summary>
        /// 根据photoId获取图片
        /// </summary>
        /// <param name="photoId"></param>
        /// <returns></returns>
        [HttpGet("{photoId:int}")]
        public async Task<ActionResult> GetPhoto(int photoId)
        {
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return Unauthorized();
            }
            
            // join Photo, Scene and Camera to get ResidentId
            var residentId = 
                await (from p in context.Photos
                        join c in context.Camera on p.CameraId equals c.CameraId
                        join s in context.Scene on c.SceneId equals s.SceneId
                        where p.PhotoId == photoId
                            select s.ResidentId).FirstAsync();
            if (residentId != resident.ResidentId)
            {
                return Forbid();
            }
            var photo = await context.Photos.FirstOrDefaultAsync(p => p.PhotoId == photoId);
            if (photo == null)
            {
                return NotFound();
            }

            return File(photo.Content, "image/*");
        }

        /// <summary>
        /// 上传图片，图片使用base64编码
        /// </summary>
        /// <param name="cameraId">图片的来源的摄像头</param>
        /// <param name="name">图片名字</param>
        /// <param name="base64Image"></param>
        /// <returns></returns>
        [HttpPut("{cameraId:int}")]
        public async Task<ActionResult<Photo>> PutPhoto(int cameraId, string name, [FromBody] string base64Image)
        {
            if (cameraId <= 0 || base64Image == "")
            {
                return BadRequest();
            }

            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return Unauthorized();
            }
            // join Camera and Scene to get ResidentId
            var residentId = await (from c in context.Camera
                    join s in context.Scene on c.SceneId equals s.SceneId
                    where c.CameraId == cameraId
                    select s.ResidentId).FirstAsync();
            if (residentId != resident.ResidentId)
            {
                return Forbid();
            }
            byte[] content = Convert.FromBase64String(base64Image);
            var photo = new Photo(cameraId, content, name);
            await context.Photos.AddAsync(photo);
            await context.SaveChangesAsync();
            return Ok(photo);
        }

        /// <summary>
        /// 通过photoId更改图片名字
        /// </summary>
        /// <param name="photoId"></param>
        /// <param name="name"></param>
        /// <returns></returns>
        [HttpPost("{photoId:int}")]
        public async Task<IActionResult> PostPhoto(int photoId, [FromBody] string name)
        {
            if (name == "" || photoId <= 0)
            {
                return BadRequest();
            }
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return Unauthorized();
            }

            var residentId =
                await (from p in context.Photos
                    join c in context.Camera on p.CameraId equals c.CameraId
                    join s in context.Scene on c.SceneId equals s.SceneId
                    where p.PhotoId == photoId
                    select s.ResidentId).FirstAsync();
            if (residentId != resident.ResidentId)
            {
                return Forbid();
            }
            var photo = await context.Photos.FirstOrDefaultAsync(p => p.PhotoId == photoId);
            if (photo == null)
            {
                return NotFound();
            }
            photo.Name = name;
            await context.SaveChangesAsync();
            return NoContent();
        }

        
        /// <summary>
        /// 删除图片，同时删除和guest的关联
        /// </summary>
        /// <param name="photoId"></param>
        /// <param name="guestId"></param>
        /// <returns></returns>
        [HttpDelete("{photoId:int}")]
        public async Task<IActionResult> DeletePhoto(int photoId, int guestId)
        {
            if (photoId <= 0 || guestId <= 0)
            {
                return BadRequest();
            }
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return Unauthorized();
            }

            var residentId =
                await (from p in context.Photos
                    join c in context.Camera on p.CameraId equals c.CameraId
                    join s in context.Scene on c.SceneId equals s.SceneId
                    where p.PhotoId == photoId
                    select s.ResidentId).FirstAsync();
            if (residentId != resident.ResidentId)
            {
                return Forbid();
            }
            // remove the photo from context.Photo and context.GuestToPhotos

            var photo = await context.Photos.FirstOrDefaultAsync(p => p.PhotoId == photoId);
            if (photo == null)
            {
                return NotFound();
            }
            var guest = context.Guests.FirstOrDefault(g => g.GuestId == guestId);
            if (guest == null)
            {
                return NotFound();
            }
            var guestToPhoto = context.GuestToPhotos.FirstOrDefault(g => g.PhotoId == photoId && g.GuestId == guestId);
            if (guestToPhoto == null)
            {
                return NotFound();
            }
            
            context.GuestToPhotos.Remove(guestToPhoto);
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
