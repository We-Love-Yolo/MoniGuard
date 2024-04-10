using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace MoniGuardAPI;

public class Camera(int cameraId, int sceneId)
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int CameraId { get; set; } = cameraId;

    [Required]
    public int SceneId { get; set; } = sceneId;
}