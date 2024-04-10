using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Identity.Web.Resource;
using MoniGuardAPI.Data;

namespace MoniGuardAPI.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class ResidentsController(MoniGuardAPIContext context) : ControllerBase
    {
        // GET: api/Residents
        //[HttpGet]
        //public async Task<ActionResult<IEnumerable<Resident>>> GetResident()
        //{
        //    return await context.Resident.ToListAsync();
        //}

        // GET: api/Residents/5
        [HttpGet("{id:int}")]
        public async Task<ActionResult<Resident>> GetResident(int id)
        {
            var resident = await context.Resident.FindAsync(id);
            if (resident == null)
            {
                return NotFound();
            }
            return resident;
        }

        // GET: /Residents/UserId/Get
        //[Authorize]
        //[HttpGet("UserId/Get")]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes:Read")]
        //public async Task<ActionResult<string>> GetUserId() => (await Task.FromResult(User.FindFirstValue(ClaimTypes.NameIdentifier)))!;

        // GET: /Residents/Resident/Get
        [Authorize]
        [HttpGet("Resident/Get")]
        [RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes:Read")]
        public async Task<ActionResult<Resident>> GetResident()
        {
            var nameIdentifier = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (nameIdentifier == null)
            {
                return BadRequest();
            }
            var resident = await context.Resident.FirstOrDefaultAsync(r => r.NameIdentifier == nameIdentifier);
            if (resident == null)
            {
                var email = User.FindFirstValue(ClaimTypes.Email);
                var nickname = User.FindFirstValue("preferred_username");

                resident = new Resident(nameIdentifier,
                    nickname ?? "MoniGuard Resident",
                    null,
                    null,
                    email);
                await context.Resident.AddAsync(resident);
                await context.SaveChangesAsync();
            }
            return resident;
        }

        // PUT: api/Residents/5
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPut("{id}")]
        public async Task<IActionResult> PutResident(int id, Resident resident)
        {
            if (id != resident.ResidentId)
            {
                return BadRequest();
            }

            context.Entry(resident).State = EntityState.Modified;

            try
            {
                await context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ResidentExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return NoContent();
        }

        // POST: api/Residents
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        [HttpPost]
        public async Task<ActionResult<Resident>> PostResident(Resident resident)
        {
            context.Resident.Add(resident);
            await context.SaveChangesAsync();

            return CreatedAtAction("GetResident", new { id = resident.ResidentId }, resident);
        }

        // DELETE: api/Residents/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteResident(int id)
        {
            var resident = await context.Resident.FindAsync(id);
            if (resident == null)
            {
                return NotFound();
            }

            context.Resident.Remove(resident);
            await context.SaveChangesAsync();

            return NoContent();
        }

        private bool ResidentExists(int id)
        {
            return context.Resident.Any(e => e.ResidentId == id);
        }
    }
}
