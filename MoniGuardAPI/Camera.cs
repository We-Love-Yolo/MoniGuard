using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace MoniGuardAPI;

public class Camera(int cameraId, string name, DateTime createdAt, int sceneId, string? description)
{

    public Camera(string name, DateTime createdAt, int sceneId, string? description) : this(0, name, createdAt, sceneId, description)
    {
    }

    /// <summary>
    /// 使用名称和描述创建一个新的摄像头。该构造方法将自动设置创建时间为当前时间。
    /// </summary>
    /// <param name="name">摄像头名。</param>
    /// <param name="description">摄像头描述。</param>
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

    [Required] 
    public string ConnectString { get; set; }
}

/// <summary>
/// 摄像头数据传输对象。
/// </summary>
/// <param name="name">摄像头名称。</param>
/// <param name="description">摄像头描述。</param>
public struct CameraDto(string name, string? description)
{
    /// <summary>
    /// 摄像头名称。
    /// </summary>
    public string Name { get; } = name;
    /// <summary>
    /// 摄像头描述。
    /// </summary>
    public string? Description { get; set; } = description;
}