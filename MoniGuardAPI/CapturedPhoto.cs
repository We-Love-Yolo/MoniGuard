using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Newtonsoft.Json;

namespace MoniGuardAPI;

public class CapturedPhoto(int cameraId, string name, DateTime capturedAt, byte[] image)
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int CapturedPhotoId { get; set; }

    [ForeignKey("Camera")]
    public int CameraId { get; set; } = cameraId;

    [Required] public string Name { get; set; } = name;

    [Required]
    public DateTime CapturedAt { get; set; } = capturedAt;


    [Required]
    public byte[] Image { get; set; } = image;

}