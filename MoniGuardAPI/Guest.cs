using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;


namespace MoniGuardAPI;

public class Guest
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int GuestId { get; set; }

    [ForeignKey("Scene")]
    public int SceneId { get; set; }

    [Required]
    [Length(2, 50)]
    public string Name { get; set; }

    [Required]
    public DateTime CreatedAt { get; set; }

    [Required] 
    public bool isAllowlList;

    [MaxLength(255)]
    [Required]
    public string Avatar { get; set; }

}