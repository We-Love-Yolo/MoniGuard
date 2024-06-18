namespace MoniGuardAPI;

/// <summary>
/// 消息。
/// </summary>
/// <param name="content"></param>
/// <param name="type"></param>
/// <param name="cameraId"></param>
/// <param name="createdAt"></param>
[method: Newtonsoft.Json.JsonConstructor]
public class Message(string content, MessageType type, int? cameraId, DateTime createdAt)
{
    public Message(string content, MessageType type, int? cameraId) : this(content, type, cameraId, DateTime.Now) { }

    public string Content { get; set; } = content;

    public MessageType Type { get; set; } = type;

    public int? CameraId { get; set; } = cameraId;

    public DateTime CreatedAt { get; set; } = createdAt;
}

public enum MessageType
{
    Info,
    Warning,
    Error
}

/// <summary>
/// 
/// </summary>
/// <param name="content"></param>
/// <param name="type"></param>
/// <param name="cameraId"></param>
[method: System.Text.Json.Serialization.JsonConstructor]
public struct MessageDto(string content, MessageType? type, int? cameraId)
{
    public string Content { get; } = content;

    public MessageType? Type { get; } = type;

    public int? CameraId { get; } = cameraId;
}