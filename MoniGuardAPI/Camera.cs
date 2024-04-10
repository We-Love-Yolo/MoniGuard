using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace MoniGuardAPI;

public class Camera(int cameraId, string name, DateTime createdAt, int sceneId, string? description)
{
    public Camera(string name, DateTime createdAt, int sceneId, string? description) : this(0, name, createdAt, sceneId, description)
    {
    }

    [JsonConstructor]
    public Camera(string name, string? description) : this(name, DateTime.Now, 0, description)
    {
    }

    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int CameraId { get; set; } = cameraId;

    [Required]
    [Length(2, 50)]
    public string Name { get; set; } = name;

    [Required]
    public DateTime CreatedAt { get; set; } = createdAt;

    [MaxLength(255)]
    public string? Description { get; set; } = description;

    [Required]
    public int SceneId { get; set; } = sceneId;
}