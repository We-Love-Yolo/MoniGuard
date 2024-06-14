using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace MoniGuardAPI;

/// <summary>
/// 摄像头实体类。
/// </summary>
/// <param name="cameraId">摄像头ID。</param>
/// <param name="name">摄像头名称。</param>
/// <param name="createdAt">摄像头创建时间。</param>
/// <param name="sceneId">摄像头所属场景的场景ID。</param>
/// <param name="description">摄像头描述。</param>
/// <param name="uniqueId">摄像头唯一ID。</param>
public class Camera(int cameraId, string name, DateTime createdAt, int sceneId, string? description, Guid uniqueId)
{
    /// <summary>
    /// 使用指定的属性初始化一个新的摄像头实例。
    /// </summary>
    /// <inheritdoc cref="Camera(int, string, DateTime, int, string?, Guid)"/>
    public Camera(string name, string? description, int sceneId, Guid uniqueId) : this(0, name, DateTime.Now, sceneId, description, uniqueId) { }

    /// <summary>
    /// 摄像头ID。
    /// </summary>
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int CameraId { get; set; } = cameraId;

    /// <summary>
    /// 摄像头名称。
    /// </summary>
    [MinLength(2)]
    [MaxLength(50)]
    public string Name { get; set; } = name;

    /// <summary>
    /// 摄像头创建时间。
    /// </summary>
    public DateTime CreatedAt { get; set; } = createdAt;

    /// <summary>
    /// 摄像头描述。
    /// </summary>
    [MaxLength(255)]
    public string? Description { get; set; } = description;

    /// <summary>
    /// 摄像头所属场景的场景ID。
    /// </summary>
    public int SceneId { get; set; } = sceneId;

    /// <summary>
    /// 摄像头唯一ID。
    /// </summary>
    public Guid UniqueId { get; set; }
}

/// <summary>
/// 摄像头数据传输对象。
/// </summary>
/// <param name="name">摄像头名称。</param>
/// <param name="description">摄像头描述。</param>
[method: JsonConstructor]
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