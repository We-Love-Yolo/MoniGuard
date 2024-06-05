using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using Microsoft.Extensions.Primitives;

namespace MoniGuardAPI;



public class GuestToPhotos(int guestToPhotoId, int guestId, int photoId)
{
    public GuestToPhotos(int guestId, int photoId) : this(0, guestId, photoId) { }

    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int GuestToPhotoId { get; set; } = guestToPhotoId;

    [Required]
    public int GuestId { get; set; } = guestId;

    [Required]
    public int PhotoId { get; set; } = photoId;

}