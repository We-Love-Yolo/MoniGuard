namespace MoniGuardAPI;

public class Message
{
    public string Content { get; set; }

    public DateTime CreatedAt { get; set; }

    public int Type { get; set; }

    public int ResidentId { get; set; }
}