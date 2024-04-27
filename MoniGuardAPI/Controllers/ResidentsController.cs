using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Identity.Web.Resource;
using MoniGuardAPI.Data;

namespace MoniGuardAPI.Controllers
{
    [Authorize]
    [Route("[controller]/[action]")]
    [ApiController]
    public class ResidentsController(MoniGuardAPIContext context) : ControllerBase
    {
        // GET: /Residents/UserId/Get
        //[Authorize]
        //[HttpGet("UserId/Get")]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes:Read")]
        //public async Task<ActionResult<string>> GetUserId() => (await Task.FromResult(User.FindFirstValue(ClaimTypes.NameIdentifier)))!;

        // GET: /Residents/GetResident
        //[Authorize]
        [HttpGet]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<ActionResult<Resident>> GetResident()
        {
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return NotFound();
            }
/*            var settings = await context.Settings
                .FirstOrDefaultAsync(s => s.ResidentId == resident.ResidentId);

            if (settings != null) return resident;

            // Create a new Settings record
            settings = new Settings(resident.ResidentId, false, false, false);
            context.Settings.Add(settings);
            await context.SaveChangesAsync();*/

            return resident;
        }

        // PUT: // GET: /Residents/PutResident
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        //[HttpPut("{id}")]
        //public async Task<IActionResult> PutResident(int id, Resident resident)
        //{
        //    if (id != resident.ResidentId)
        //    {
        //        return BadRequest();
        //    }

        //    context.Entry(resident).State = EntityState.Modified;

        //    try
        //    {
        //        await context.SaveChangesAsync();
        //    }
        //    catch (DbUpdateConcurrencyException)
        //    {
        //        if (!ResidentExists(id))
        //        {
        //            return NotFound();
        //        }
        //        else
        //        {
        //            throw;
        //        }
        //    }

        //    return NoContent();
        //}

        // PUT: /Residents/PutResident
        //[Authorize]
        [HttpPut]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public async Task<IActionResult> PutResident(Resident resident)
        {
            var authorizedResident = await GetAuthorizedResident();
            if (authorizedResident == null)
            {
                return NotFound();
            }

            var id = authorizedResident.ResidentId;
            var nameIdentifier = authorizedResident.NameIdentifier;
            if (id != resident.ResidentId || nameIdentifier != resident.NameIdentifier)
            {
                return BadRequest();
            }

            context.Entry(authorizedResident).State = EntityState.Detached;

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
            }

            return NoContent();
        }

        //[Authorize]
        [HttpGet]
        //[RequiredScope(RequiredScopesConfigurationKey = "AzureAd:Scopes")]
        public IActionResult GetAvatar() => Redirect("https://avatar.iran.liara.run/public");

        // POST: api/Residents
        // To protect from overposting attacks, see https://go.microsoft.com/fwlink/?linkid=2123754
        //[HttpPost]
        //public async Task<ActionResult<Resident>> PostResident(Resident resident)
        //{
        //    context.Resident.Add(resident);
        //    await context.SaveChangesAsync();

        //    return CreatedAtAction("GetResident", new { id = resident.ResidentId }, resident);
        //}

        //// DELETE: api/Residents/5
        //[HttpDelete("{id}")]
        //public async Task<IActionResult> DeleteResident(int id)
        //{
        //    var resident = await context.Resident.FindAsync(id);
        //    if (resident == null)
        //    {
        //        return NotFound();
        //    }

        //    context.Resident.Remove(resident);
        //    await context.SaveChangesAsync();

        //    return NoContent();
        //}

        [HttpGet]
        public async Task<ActionResult<Settings>> GetSettings()
        {
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return NotFound();
            }

            var settings = await context.Settings.FirstOrDefaultAsync(s => s.ResidentId == resident.ResidentId);
            if (settings == null)
            {
                settings = new Settings();
                settings.ResidentId = resident.ResidentId;
                await context.Settings.AddAsync(settings);
                await context.SaveChangesAsync();
            }

            return settings;
        }

        [HttpPut]
        public async Task<IActionResult> PutSettings(Settings settings)
        {
            var resident = await GetAuthorizedResident();
            if (resident == null)
            {
                return NotFound();
            }

            if (resident.ResidentId != settings.ResidentId)
            {
                return BadRequest();
            }

            context.Entry(settings).State = EntityState.Modified;
            try
            {
                await context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ResidentExists(settings.ResidentId))
                {
                    return NotFound();
                }
            }

            return NoContent();
        }

        private bool ResidentExists(int id)
        {
            return context.Resident.Any(e => e.ResidentId == id);
        }

        private async Task<Resident?> GetAuthorizedResident()
        {
            var nameIdentifier = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (nameIdentifier == null)
            {

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
