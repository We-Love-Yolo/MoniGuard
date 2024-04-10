using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;

namespace MoniGuardAPI;

public class Scene(int sceneId, string name, int residentId)
{
    public Scene(string name, int residentId) : this(0, name, residentId)
    {
    }

    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int SceneId { get; set; } = sceneId;

    [Required]
    [MinLength(2)]
    [MaxLength(50)]
    public string Name { get; set; } = name;

    [Required]
    public int ResidentId { get; set; } = residentId;
}