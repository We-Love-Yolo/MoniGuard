using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace MoniGuardAPI
{
    public class Face(int faceId, int guestId, string? name, DateTime capturedAt, byte[]? hash, byte[]? content)
    {
        public Face(int faceId, int guestId, string? name, DateTime capturedAt, byte[]? hash) :
            this(faceId,guestId,name,capturedAt,hash,null)
        {

        }

        public Face() : this(0, 0, null, DateTime.Now, null)
        { 
        }


        [Key]
        public int FaceId { get; set; } = faceId;
        [ForeignKey("Guest")]
        public int GuestId { get; set; } = guestId;
        public string? Name { get; set; } = name;
        public DateTime CapturedAt { get; set; } = capturedAt;
        public byte[]? Hash { get; set; } = hash;

        [JsonIgnore]
        public byte[]? Content { get; set; } = content;
    }
}
