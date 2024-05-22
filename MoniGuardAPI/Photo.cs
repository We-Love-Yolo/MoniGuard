
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace MoniGuardAPI;

public class Photo
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int PhotoId { get; set; }

    [ForeignKey("Camera")]
    public int CameraId { get; set; }

    [Required]
    public DateTime CreatedAt { get; set; }

    [Required]
    public string Url { get; set; }
    // {server ip}/xxx/{sceneId}/{md5}.jpg

    [MaxLength(255)]
    [Required]
    public string Name { get; set; }
}
