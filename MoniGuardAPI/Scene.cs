using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace MoniGuardAPI;

public class Scene(int sceneId, int residentId)
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int SceneId { get; set; } = sceneId;

    [Required]
    public int ResidentId { get; set; } = residentId;
}