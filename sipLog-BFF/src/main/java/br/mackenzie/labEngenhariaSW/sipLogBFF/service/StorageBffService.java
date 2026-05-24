package br.mackenzie.labEngenhariaSW.sipLogBFF.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageBffService {

    private final S3Client s3Client;

    @Value("${AWS_S3_BUCKET_NAME}")
    private String bucketName;

    @Value("${AWS_REGION}")
    private String region;

    public StorageBffService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String enviarImagemParaS3(MultipartFile file) {
        // Extrai a extensão do arquivo original (ex: .jpg, .png)
        String extensao = "";
        String nomeOriginal = file.getOriginalFilename();
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }

        // Gera um nome único para a chave do objeto no S3
        String nomeArquivoS3 = "experiencias/" + UUID.randomUUID().toString() + extensao;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nomeArquivoS3)
                    .contentType(file.getContentType())
                    .build();

            // Executa o upload síncrono enviando o Inputstream e o tamanho do arquivo
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Constrói e retorna a URL pública de acesso ao objeto
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, nomeArquivoS3);

        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler os bytes do arquivo para upload no S3", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao interagir com a API do Amazon S3", e);
        }
    }
}