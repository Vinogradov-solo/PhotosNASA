import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
Работа с API NASA.
Программа скачивает фотоснимки за январь 2022 года (можно сделать любую дату, в том числе, по запросу пользователя)
и выводит описание каждого снимка.
 */

public class Main
{
    public static void main(String[] args) throws IOException
    {
        String page;
        String apiFirst = "https://api.nasa.gov/planetary/apod?api_key=QqwQ0sYSSVqsuNDxdE1oX7PvaeCP0VVwynikINrT&date=2022-01-";
        String apiEnd;
        for (int i = 1; i <= 31; i++)
        {
            if (i < 10)
                apiEnd = "0" + i;
            else
                apiEnd = Integer.toString(i);

            page = downloadWebPage(apiFirst + apiEnd);
            int expBegin = page.lastIndexOf("explanation");
            int expEnd = page.lastIndexOf("\",\"hdurl\"");

            String explanation;
            String url;
            String image;
            PrintWriter writer;
            try
            {
                explanation = page.substring(expBegin + 14, expEnd);
                System.out.println("Описание фото на " + i + " января:\n" + explanation);
                int urlStart = page.lastIndexOf("url");
                int urlEnd = page.lastIndexOf("}");
                url = page.substring(urlStart + 6, urlEnd - 1);
                image = downloadWebPage(url);
                writer = new PrintWriter("picture.jpg");
                writer.print(image);
                writer.close();
            } catch (StringIndexOutOfBoundsException e)
            {
                System.err.println("На " + i + " января фото не найдено.\n");
                continue;
            }
            try (InputStream in = new URL(url).openStream())
            {
                Files.copy(in, Paths.get("page" + i + ".jpg"));
                System.out.println("Фото успешно сохранено.\n");
            }
        }
    }

    private static String downloadWebPage(String url) throws IOException
    {
        StringBuilder result = new StringBuilder();
        String line;
        URLConnection urlConnection = new URL(url).openConnection();
        try(InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is)))
        {
            while((line = br.readLine())  != null)
            {
                result.append(line);
            }
        }
        return result.toString();
    }
}
