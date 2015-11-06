import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Example how to use multipart/form encoded POST request.
 */
public class ClientMultipartFormPost {

    public static void main(String[] args) throws Exception {
        if (args.length != 1)  {
            System.out.println("File path not given");
            System.exit(1);
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {

            // url da requisicao
            HttpPost httppost = new HttpPost("http://sl.wancharle.com.br/note/create/");

            // Arquivo binario que sera enviado ( a foto no seu caso) 
            FileBody bin = new FileBody(new File(args[0]),"image/jpeg");

            // parametro latitude e longitude obrigatorio
            StringBody latitude = new StringBody("0.0", ContentType.TEXT_PLAIN);
            StringBody longitude = new StringBody("0.0", ContentType.TEXT_PLAIN);

            // id do usuario "allan"  senha 123456
            StringBody userID = new StringBody("563c083e803853c66c143c82", ContentType.TEXT_PLAIN);
            // id da notebook "ProjetoAmbiental" anotacoes podem ser vistas em http://sl.wancharle.com.br/note/?notebook=5612bad5b8ba98cc5eb9e1c1
            StringBody notebookID = new StringBody("5612bad5b8ba98cc5eb9e1c1", ContentType.TEXT_PLAIN); 
            
            // parametro qualquer que vc queira adicionar a anotacao
            StringBody texto = new StringBody("texto qualquer", ContentType.TEXT_PLAIN); 
            StringBody fotoURL = new StringBody("true", ContentType.TEXT_PLAIN); 

            // montagem do json + foto
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("foto", bin)
                    .addPart("fotoURL", fotoURL)
                    .addPart("user", userID)
                    .addPart("notebook", notebookID)
                    .addPart("latitude", latitude)
                    .addPart("longitude", longitude)
                    .addPart("texto", texto)
                    .build();


            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                    System.out.println("----------------------------------------");
                   resEntity.writeTo(System.out);
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}
