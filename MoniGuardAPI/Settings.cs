using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace MoniGuardAPI
{
    public class Settings(int residentId, bool receiveWarning, bool receiveNewGuest, bool healthNotice)
    {
        [Key]
        public int SettingsId { get; set; }

        [Required]
        [ForeignKey("Resident")]
        public int ResidentId { get; set; } = residentId;

        [Required]
        public bool ReceiveWarning { get; set; } = receiveWarning;
        [Required]
        public bool ReceiveNewGuest { get; set; } = receiveNewGuest;
        [Required]
        public bool HealthNotice { get; set; } = healthNotice;

        public Settings(): this(0,false, false, false)
        {
        }
    }
}
