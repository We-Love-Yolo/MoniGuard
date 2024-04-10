using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Identity.Web.Resource;
using MoniGuardAPI.Data;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace MoniGuardAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class ScenesController(MoniGuardAPIContext context) : ControllerBase
    {
        // GET: Scenes/GetScenes
        [HttpGet("GetScenes")]
        [RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<ActionResult<List<Scene>>> GetScenes()
        {
            var nameIdentifier = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var resident = await context.Resident.FirstOrDefaultAsync(r => r.NameIdentifier == nameIdentifier);
            if (resident == null)
            {
                return NotFound();
            }
            return await context.Scene.Where(s => s.ResidentId == resident.ResidentId).ToListAsync();
        }

        // GET api/<ScenesController>/5
        [HttpGet("{id}")]
        public string Get(int id)
        {
            return "value";
        }

        // POST api/<ScenesController>
        [HttpPost]
        public void Post([FromBody] string value)
        {
        }

        // PUT api/<ScenesController>/5
        [HttpPut("{id}")]
        public void Put(int id, [FromBody] string value)
        {
        }

        // DELETE api/<ScenesController>/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}
