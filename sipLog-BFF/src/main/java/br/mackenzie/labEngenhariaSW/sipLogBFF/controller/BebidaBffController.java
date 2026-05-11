@RestController
@RequestMapping("/api/v1/bebidas")
public class BebidaBffController {

    private final BebidaBffService bebidaService;

    public BebidaBffController(BebidaBffService bebidaService) {
        this.bebidaService = bebidaService;
    }

    // ITEM 3: Buscar Bebidas para autocompletar
    @GetMapping("/buscar")
    public ResponseEntity<List<BebidaResumoDTO>> buscarBebidas(@RequestParam String q) {
        List<BebidaResumoDTO> bebidas = bebidaService.buscarNoCatalogo(q);
        return ResponseEntity.ok(bebidas);
    }
}