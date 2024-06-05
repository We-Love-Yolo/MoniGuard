
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Microsoft.Extensions.Primitives;

namespace MoniGuardAPI;

public class Photo(int photoId, int cameraId, DateTime createdAt, byte[] content, string name)
{
    public Photo(int cameraId, byte[] content, string name): this(0, cameraId, DateTime.Now, content, name) { }


    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int PhotoId { get; set; } = photoId;

    [ForeignKey("Camera")] 
    public int CameraId { get; set; } = cameraId;

    [Required]
    public DateTime CreatedAt { get; set; } = createdAt;
    
    [JsonIgnore]
    [Required] 
    public byte[]? Content { get; set; } = content;
    // {server ip}/xxx/{sceneId}/{md5}.jpg

    [MaxLength(255)] 
    [Required] 
    public string Name { get; set; } = name;
}
