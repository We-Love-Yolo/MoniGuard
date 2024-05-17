﻿using Microsoft.EntityFrameworkCore;

namespace MoniGuardAPI.Data;

public class MoniGuardAPIContext(DbContextOptions<MoniGuardAPIContext> options) : DbContext(options)
{
    public DbSet<Resident> Resident { get; set; } = default!;

    public DbSet<Scene> Scene { get; set; } = default!;

    public DbSet<Camera> Camera { get; set; } = default!;

    public DbSet<Settings> Settings { get; set; } = default!;

    public DbSet<Guest> Guests { get; set; } = default!;

    public DbSet<Photo> Photos { get; set; } = default!;
}