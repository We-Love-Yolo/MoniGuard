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

namespace MoniGuardAPI.Controllers
{


    //this controller is used to test the redis cache
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class RedisTestController(IDistributedCache distributedCache, MoniGuardAPIContext context) : ControllerBase
    {
        private readonly IDistributedCache _distributedCache = distributedCache;

        [HttpPost("UpdateMessages")]
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
                var messages = JsonSerializer.Deserialize<List<Message>>(Encoding.UTF8.GetString(messagesStr));
                messages.Add(message);
                var json = JsonSerializer.Serialize(messages);
                await _distributedCache.SetStringAsync($"{id}_test", json, cacheEntryOptions);
            }
            //todo: add the message to the database
            return Ok();
        }


        [HttpGet("GetMessages")]
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
