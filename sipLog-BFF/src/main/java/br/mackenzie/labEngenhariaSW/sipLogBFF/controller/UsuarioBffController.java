@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioBffController {

    private final UsuarioBffService usuarioService;

    public UsuarioBffController(UsuarioBffService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ITEM 1: Sincronizar usuário após o login no Keycloak
    @PostMapping("/sync")
    public ResponseEntity<Void> sincronizarUsuario(@AuthenticationPrincipal Jwt principal) {
        // O Service vai extrair os dados do JWT e avisar a Core API
        usuarioService.sincronizarComCoreApi(principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ITEM 1: Buscar o próprio perfil (Minha Estante)
    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilDTO> getMeuPerfil(@AuthenticationPrincipal Jwt principal) {
        UsuarioPerfilDTO perfil = usuarioService.buscarMeuPerfil(principal.getSubject());
        return ResponseEntity.ok(perfil);
    }

    // ITEM 1: Buscar o perfil de um amigo
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioPerfilDTO> getPerfilUsuario(@PathVariable Long idUsuario, @AuthenticationPrincipal Jwt principal) {
        UsuarioPerfilDTO perfil = usuarioService.buscarPerfilDeTerceiro(idUsuario, principal.getSubject());
        return ResponseEntity.ok(perfil);
    }

    // ITEM 4: Seguir / Deixar de Seguir (Rede Social)
    @PostMapping("/{idAlvo}/seguir")
    public ResponseEntity<Void> alternarSeguir(@PathVariable Long idAlvo, @AuthenticationPrincipal Jwt principal) {
        usuarioService.alternarSeguirUsuario(principal.getSubject(), idAlvo);
        return ResponseEntity.ok().build();
    }
}