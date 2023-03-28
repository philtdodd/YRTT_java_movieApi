package com.techreturners.movieApi.apiProxy;

import com.techreturners.movieApi.vo.Movies;
import com.techreturners.movieApi.vo.Pagination;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.techreturners.movieApi.util.UtilConstants.*;

@Service
@NoArgsConstructor
public class MovieApiProxyImpl extends ApiProxyBase<Movies> implements MovieApiProxy {

    @Value("${my-app-url}")
    private String myAppUrl;

    @Override
    public Movies retriveMoviesByYear(Long year, Integer page) throws IOException {

        String apiUrl = BASE_URL + MOVIES;
        String apiEndpoint = Objects.nonNull(page)? "/byYear/" + year+"?page="+page : "/byYear/" + year;
        Movies movies = retrieveData(apiUrl+apiEndpoint);
        if(Objects.nonNull(movies) && Objects.nonNull(movies.getLinks())) {
            Pagination links = new Pagination();
            links.setNext(getPaginationLink(movies.getLinks().getNext(), "/byYear/"+year));
            links.setPrevious(getPaginationLink(movies.getLinks().getPrevious(), "/byYear/"+year));
            movies.setLinks(links);
        }
        return movies;
    }

    @Override
    public Movies retriveMoviesOrderByRating(Integer page) throws IOException {
        String apiUrl = BASE_URL + MOVIES + ORDER_BY_RATING;
        String apiEndpoint = Objects.nonNull(page)? "/?page="+page : "";
        Movies movies = retrieveData(apiUrl+ apiEndpoint);
        if(Objects.nonNull(movies) && Objects.nonNull(movies.getLinks())) {
            Pagination links = new Pagination();
            links.setNext(getPaginationLink(movies.getLinks().getNext(), ORDER_BY_RATING));
            links.setPrevious(getPaginationLink(movies.getLinks().getPrevious(), ORDER_BY_RATING));
            movies.setLinks(links);
        }
        return movies;
    }

    @Override
    public Movies retriveMovieIdByTitle(String title) throws IOException{
        String apiUrl = BASE_URL + MOVIES + MOVIEID_BY_TITLE+title;
        return retrieveData(apiUrl);
    }

    @Override
    public Movies retriveMoviesByGenre(String genre, Integer page) throws IOException {
        String apiUrl = BASE_URL + MOVIES + "/byGen/"+genre;
        String apiEndpoint = Objects.nonNull(page)? "/?page="+page : "";
        Movies movies = retrieveData(apiUrl+ apiEndpoint);
        if(Objects.nonNull(movies) && Objects.nonNull(movies.getLinks())) {
            Pagination links = new Pagination();
            links.setNext(getPaginationLink(movies.getLinks().getNext(), "/byGenre/"+genre));
            links.setPrevious(getPaginationLink(movies.getLinks().getPrevious(), "/byGenre/"+genre));
            movies.setLinks(links);
        }
        return movies;
    }

    private String getPaginationLink(String link, String url) {
        if(Objects.nonNull(link)) {
            MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(link).build().getQueryParams();
            List<String> pages = params.get("page");
            if(Objects.nonNull(pages) && !pages.isEmpty()) {
                return String.format("%s/api/v1/movie%s?page=%s", myAppUrl, url, params.get("page").get(0));
            } else {
                return String.format("%s/api/v1/movie%s", myAppUrl, url);
            }
        }
        return null;
    }

    @Override
    protected Class<Movies> getType() {
        return Movies.class;
    }
}


