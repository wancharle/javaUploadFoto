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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.methods.HttpGet;


/**
 * Example how to use multipart/form encoded POST request.
 */
public class ClientMultipartFormPost {
    static BasicCookieStore cookieStore = new BasicCookieStore();
    static RequestConfig requestConfig ;
    static CloseableHttpClient httpclient;
        
    public static void init() throws Exception{
        httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        requestConfig=RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .setCookieSpec(CookieSpecs.STANDARD)
            .build();

        // login chamado apenas no init
        login();
    }

    public static void login() throws Exception{
            HttpGet httpget = new HttpGet("http://sl.wancharle.com.br/user/login/?username=wan&password=123456");
            httpget.setConfig(requestConfig);
            httpget.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Firefox/3.6.13");

            CloseableHttpResponse response1 = httpclient.execute(httpget);
            try{
                HttpEntity entity = response1.getEntity();

                System.out.println("Login form get: " + response1.getStatusLine());
                EntityUtils.consume(entity);
            } finally {
                response1.close();
            }

         
        }

    public static void  addNote (String filename)  throws Exception {
        // url da requisicao
        HttpPost httppost = new HttpPost("http://sl.wancharle.com.br/note/create/");
        httppost.setConfig(requestConfig);

        // Arquivo binario que sera enviado ( a foto no seu caso) 
        FileBody bin = new FileBody(new File(filename),"image/jpeg");

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
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1)  {
            System.out.println("File path not given");
            System.exit(1);
        }
        try{
            init();
            addNote(args[0]);
        }finally {
            httpclient.close();
        }
    }
}
