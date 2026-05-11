@RestController
@RequestMapping("/api/v1/upload")
public class UploadBffController {

    private final StorageBffService storageService;

    public UploadBffController(StorageBffService storageService) {
        this.storageService = storageService;
    }

    // ITEM 3: Upload de Imagem (Recebe MultipartFile, devolve a URL String)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDTO> uploadFoto(@RequestParam("file") MultipartFile file) {
        String urlGerada = storageService.enviarImagemParaS3(file);
        return ResponseEntity.ok(new UploadResponseDTO(urlGerada));
    }
}