using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.Identity.Web;
using Microsoft.EntityFrameworkCore;
using MoniGuardAPI.Data;
using Microsoft.OpenApi.Models;
using Swashbuckle.AspNetCore.Filters;
using System.Reflection;

var builder = WebApplication.CreateBuilder(args);
builder.Services.AddDbContext<MoniGuardAPIContext>(options =>
{
    options.UseSqlServer(builder.Configuration.GetConnectionString("MoniGuardAPIContext") ??
                         throw new InvalidOperationException("Connection string 'MoniGuardAPIContext' not found."));
    options.EnableSensitiveDataLogging();
});
builder.Services.AddStackExchangeRedisCache(options =>
{
    options.Configuration = builder.Configuration.GetConnectionString("Redis");

    options.InstanceName = "Redis_";
});
builder.Services.AddMemoryCache();

var azureAdConfigurationSection = builder.Configuration.GetSection("AzureAd");
// Add services to the container.
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddMicrosoftIdentityWebApi(azureAdConfigurationSection);

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new OpenApiInfo
    {
        Version = "v1",
        Title = "MoniGuard API",
        Description = "MoniGuard 项目的 Web API 文档。",
        TermsOfService = new Uri("https://example.com/terms"),
        Contact = new OpenApiContact
        {
            Name = "联系方式",
            Url = new Uri("https://example.com/contact")
        },
        License = new OpenApiLicense
        {
            Name = "最终用户协议（EULA）",
            Url = new Uri("https://example.com/license")
        }
    });

    // Add JWT Bearer authentication
    options.AddSecurityDefinition("oauth2", new OpenApiSecurityScheme
    {
        Type = SecuritySchemeType.OAuth2,
        Flows = new OpenApiOAuthFlows
        {
            AuthorizationCode = new OpenApiOAuthFlow
            {
                AuthorizationUrl = new Uri($"https://login.microsoftonline.com/{azureAdConfigurationSection["TenantId"]}/oauth2/v2.0/authorize"),
                TokenUrl = new Uri($"https://login.microsoftonline.com/{azureAdConfigurationSection["TenantId"]}/oauth2/v2.0/token"),
                Scopes = new Dictionary<string, string>
                {
                    //{ "openid", "Sign in to Microsoft Entra" },
                    //{ "profile", "View your profile" },
                    //{ "email", "Access your email address" },
                    { $"api://{azureAdConfigurationSection["ClientId"]}/MoniGuard.Read", "读取型MoniGuard API" },
                    { $"api://{azureAdConfigurationSection["ClientId"]}/MoniGuard.ReadWrite", "读写型MoniGuard API" }
                }
            }
        }
    });

    options.OperationFilter<SecurityRequirementsOperationFilter>();

    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    options.IncludeXmlComments(xmlPath, true);
});
if (!builder.Environment.IsDevelopment())
{
    builder.Services.AddLettuceEncrypt();
}



var app = builder.Build();

// Configure the HTTP request pipeline.
//if (app.Environment.IsDevelopment())
//{
//    app.UseSwagger();
//    app.UseSwaggerUI();
//}

app.UseSwagger();
app.UseSwaggerUI(options =>
{
    options.OAuthClientId(azureAdConfigurationSection["ClientId"]);
    options.OAuthScopes($"api://{azureAdConfigurationSection["ClientId"]}/MoniGuard.Read");
        //$"api://{azureAdConfigurationSection["ClientId"]}/MoniGuard.ReadWrite");
    options.OAuthUsePkce();
});

app.UseHttpsRedirection();

app.UseAuthentication();

app.UseAuthorization();

app.MapControllers();

app.Run();
