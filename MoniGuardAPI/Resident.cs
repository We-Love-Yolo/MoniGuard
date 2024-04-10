using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Newtonsoft.Json;

namespace MoniGuardAPI;

public class Resident(int residentId, string nameIdentifier, string? nickname, string? avatar, string? phone, string? email)
{
    public Resident(string nameIdentifier, string? nickname, string? avatar, string? phone, string? email) : this(0, nameIdentifier, nickname, avatar, phone, email)
    {
    }

    public Resident() : this(string.Empty, string.Empty, string.Empty, string.Empty, string.Empty)
    {
    }

    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int ResidentId { get; set; } = residentId;

    [Required]
    [MaxLength(50)]
    [JsonIgnore]
    public string NameIdentifier { get; set; } = nameIdentifier;

    [MaxLength(63)]
    public string? Nickname { get; set; } = nickname;

    [MaxLength(255)]
    public string? Avatar { get; set; } = avatar;

    [Phone]
    [MaxLength(20)]
    public string? Phone { get; set; } = phone;

    [EmailAddress]
    [MaxLength(255)]
    public string? Email { get; set; } = email;
}