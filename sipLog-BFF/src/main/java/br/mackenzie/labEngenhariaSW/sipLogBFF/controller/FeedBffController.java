@RestController
@RequestMapping("/api/v1/feed")
public class FeedBffController {

    private final FeedBffService feedService;

    public FeedBffController(FeedBffService feedService) {
        this.feedService = feedService;
    }

    // Rotas existentes... (Global, Amigos, Me)

    // ITEM 2: Feed de Terceiros (Quando clico no perfil de alguém)
    @GetMapping("/usuarios/{idUsuario}")
    public ResponseEntity<PaginaBffDTORecive<FeedResponseDTO>> getFeedDeUsuario(
            @PathVariable Long idUsuario, 
            @RequestParam(defaultValue = "0") int pagina,
            @AuthenticationPrincipal Jwt principal) {
        
        PaginaBffDTORecive<FeedResponseDTO> feed = feedService.buscarFeedDeTerceiro(idUsuario, pagina, principal.getSubject());
        return ResponseEntity.ok(feed);
    }
}