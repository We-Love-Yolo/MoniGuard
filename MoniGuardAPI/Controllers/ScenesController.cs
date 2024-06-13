using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Identity.Web.Resource;
using MoniGuardAPI.Data;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace MoniGuardAPI.Controllers
{
    [Authorize]
    [Route("[controller]/[action]")]
    [ApiController]
    public class ScenesController(MoniGuardAPIContext context, IMemoryCache cache) : ControllerBase
    {
        // GET: /Scenes/GetScenes
        //[Authorize]
        [HttpGet]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<ActionResult<List<Scene>>> GetScenes()
        {
            var nameIdentifier = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var residentId = await GetAuthorizedResidentId();
            var scenes = await context.Scene.Where(s => s.ResidentId == residentId).ToListAsync();
            if (scenes.Count == 0)
            {
                var scene = new Scene("默认", (int)residentId);
                await context.Scene.AddAsync(scene);
                await context.SaveChangesAsync();
            }
            scenes = await context.Scene.Where(s => s.ResidentId == residentId).ToListAsync();
            return scenes;
        }

        // GET: /Scenes/GetCameras
        //[Authorize]
        [HttpGet("{sceneId:int}")]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<ActionResult<List<Camera>>> GetCameras(int sceneId)
        {
            var residentId = await GetAuthorizedResidentId();
            if (residentId == null)
            {
                return NotFound();
            }
            var scene = await context.Scene.FirstOrDefaultAsync(s => s.SceneId == sceneId);
            if (scene == null || scene.ResidentId != residentId)
            {
                return NotFound();
            }
            return await context.Camera.Where(c => c.SceneId == sceneId).ToListAsync();
        }

        // POST /Scenes/PostScene
        //[Authorize]
        [HttpPost("{sceneName:length(2, 50)}")]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<IActionResult> PostScene(string sceneName)
        {
            var residentId = await GetAuthorizedResidentId();
            if (residentId == null)
            {
                return NotFound();
            }
            await context.Scene.AddAsync(new Scene(sceneName, (int)residentId));
            await context.SaveChangesAsync();
            return NoContent();
        }

        // POST /Scenes/PostCamera
        //[Authorize]
        [HttpPost("{sceneId:int}")]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<IActionResult> PostCamera(int sceneId, [FromBody] Camera camera)
        {
            var residentId = await GetAuthorizedResidentId();
            if (residentId == null)
            {
                return NotFound();
            }
            var scene = await context.Scene.FirstOrDefaultAsync(s => s.SceneId == sceneId);
            if (scene == null || scene.ResidentId != residentId)
            {
                return Unauthorized();
            }
            camera.SceneId = sceneId;
            await context.Camera.AddAsync(camera);
            await context.SaveChangesAsync();
            return NoContent();
        }

        // DELETE /Scenes/DeleteScene
        //[Authorize]
        [HttpDelete("{sceneId:int}")]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<IActionResult> DeleteScene(int sceneId)
        {
            var scene = context.Scene.FirstOrDefault(s => s.SceneId == sceneId);
            if (scene == null)
            {
                return NotFound();
            }
            if (scene.ResidentId != await GetAuthorizedResidentId())
            {
                return Unauthorized();
            }
            context.Scene.Remove(scene);
            await context.SaveChangesAsync();
            return NoContent();
        }

        // DELETE /Scenes/DeleteCamera
        //[Authorize]
        [HttpDelete("{cameraId:int}")]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<IActionResult> DeleteCamera(int cameraId)
        {
            var camera = context.Camera.FirstOrDefault(c => c.CameraId == cameraId);
            if (camera == null)
            {
                return NotFound();
            }
            var scene = await context.Scene.FirstOrDefaultAsync(s => s.SceneId == camera.SceneId);
            if (scene == null || scene.ResidentId != await GetAuthorizedResidentId())
            {
                return Unauthorized();
            }
            context.Camera.Remove(camera);
            await context.SaveChangesAsync();
            return NoContent();
        }

        [HttpPost]
        public async Task<ActionResult<Dictionary<string, int>>> RequestAddCamera(string connectString)
        {
            var residentId = await GetAuthorizedResidentId();
            if (residentId == null)
            {
                return NotFound();
            }
            var random = new Random();
            var randomNumber = random.Next(100000, 1000000);

            var key = residentId.ToString() + randomNumber.ToString();
            cache.Set(key, connectString, new MemoryCacheEntryOptions().SetSlidingExpiration(TimeSpan.FromMinutes(5)));
            var dictionary = new Dictionary<string, int>
            {
                {"key", randomNumber}
            };
            
            return dictionary;
        }

        [HttpGet]
        public async Task<ActionResult<string>> GetCameraConnectString(int key, string name, int sceneId, string? description)
        {
            var residentId = await GetAuthorizedResidentId();
            if (residentId == null)
            {
                return NotFound();
            }
            var connectString = cache.Get<string>(residentId.ToString() + key);
            if (connectString == null)
            {
                return NotFound();
            }
            cache.Remove(residentId.ToString() + key);
            var camera = new Camera(name, DateTime.Now, sceneId, description)
            {
                ConnectString = connectString
            };
            await context.Camera.AddAsync(camera);
            await context.SaveChangesAsync();
            return connectString;
        }

        [HttpGet("{sceneId:int}")]
        public async Task<ActionResult<List<Guest>>> GetGuest(int sceneId)
        {
            var residentId = await GetAuthorizedResidentId();
            if (residentId == null)
            {
                return NotFound();
            }
            var scene = await context.Scene.FirstOrDefaultAsync(s => s.SceneId == sceneId);
            if (scene == null || scene.ResidentId != residentId)
            {
                return Unauthorized();
            }

            return await context.Guests.Where(g => g.SceneId == sceneId).ToListAsync();
        }

        [HttpPost("{guestId:int}")]
        public async Task<IActionResult> PostGuest(int guestId, [FromBody] Guest guest)
        {
            var residentId = await GetAuthorizedResidentId();
            if (residentId == null)
            {
                return NotFound();
            }
            
            context.Entry(guest).State = EntityState.Modified;
            try
            {
                await context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                return BadRequest();
            }
            return NoContent();
        }

        [HttpDelete("{guestId:int}")]
        public async Task<IActionResult> DeleteGuest(int guestId)
        {
            var guest = context.Guests.FirstOrDefault(g => g.GuestId == guestId);
            if (guest == null)
            {
                return NotFound();
            }
            var scene = await context.Scene.FirstOrDefaultAsync(s => s.SceneId == guest.SceneId);
            if (scene == null || scene.ResidentId != await GetAuthorizedResidentId())
            {
                return Unauthorized();
            }
            context.Guests.Remove(guest);
            var guestToPhotos = context.GuestToPhotos.Where(g => g.GuestId == guestId).ToList();
            context.GuestToPhotos.RemoveRange(guestToPhotos);
            await context.SaveChangesAsync();
            return NoContent();
        }

        private async Task<int?> GetAuthorizedResidentId()
        {
            var nameIdentifier = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (nameIdentifier == null)
            {
                return null;
            }

            var resident = await context.Resident.FirstOrDefaultAsync(r => r.NameIdentifier == nameIdentifier);
            if (resident != null)
            {
                return resident.ResidentId;
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
            return resident.ResidentId;
        }
    }
}
