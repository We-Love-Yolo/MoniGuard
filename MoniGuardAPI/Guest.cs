using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace MoniGuardAPI;

/// <summary>
/// 来宾信息。
/// </summary>
/// <param name="guestId">来宾 ID。</param>
/// <param name="sceneId">来宾所属场景 ID。</param>
/// <param name="name">来宾名称。</param>
/// <param name="createdAt">来宾创建时间。</param>
/// <param name="isWhitelisted">来宾是否已经加入白名单。</param>
/// <param name="faceEncoding">来宾的人脸数据。</param>
public class Guest(int guestId, int sceneId, string name, DateTime createdAt, bool isWhitelisted, byte[]? faceEncoding)
{
    /// <summary>
    /// 创建来宾信息的构造方法。
    /// </summary>
    /// <inheritdoc cref="Guest"/>
    public Guest(int sceneId, string name, bool isWhitelisted) :this(0, sceneId, name, DateTime.Now, isWhitelisted, null) { }

    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int GuestId { get; set; } = guestId;

    /// <summary>
    /// 来宾所属场景 ID。
    /// </summary>
    [ForeignKey("Scene")]
    public int SceneId { get; set; } = sceneId;

    /// <summary>
    /// 来宾名称。
    /// </summary>
    [Length(2, 50)]
    public string Name { get; set; } = name;

    /// <summary>
    /// 来宾创建时间。
    /// </summary>
    public DateTime CreatedAt { get; set; } = createdAt;

    /// <summary>
    /// 来宾是否已经加入白名单。
    /// </summary>
    public bool IsWhitelisted { get; set; } = isWhitelisted;

    /// <summary>
    /// 来宾的人脸数据。
    /// </summary>
    public byte[]? FaceEncoding { get; set; } = faceEncoding;

}

/// <summary>
/// 来宾数据传输对象。
/// </summary>
/// <param name="name">来宾名称。</param>
/// <param name="isWhitelisted">是否加入白名单。该值可空，空值表示不加入。</param>
[method:JsonConstructor]
public struct GuestDto(string name, bool? isWhitelisted)
{
    /// <summary>
    /// 来宾名称。
    /// </summary>
    public string Name { get; } = name;

    /// <summary>
    /// 是否加入白名单。该值可空，空值表示不加入。
    /// </summary>
    public bool? IsWhitelisted { get; } = isWhitelisted;
}