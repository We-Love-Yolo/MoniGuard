using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using System.Data;


namespace MoniGuardAPI;

public class Guest(int guestId, int sceneId, string name, DateTime createdAt, bool isAllowed)
{
    public Guest(int sceneId ) :this(-1, sceneId, "Amber", DateTime.Now, false) { }

    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int GuestId { get; set; } = guestId;

    [ForeignKey("Scene")]
    public int SceneId { get; set; } = sceneId;

    [Required]
    [Length(2, 50)]
    public string Name { get; set; } = name;

    [Required]
    public DateTime CreatedAt { get; set; } = createdAt;

    [Required]
    public bool IsAllowed { get; set; } = isAllowed;

}