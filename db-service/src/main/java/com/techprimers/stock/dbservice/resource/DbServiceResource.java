package com.techprimers.stock.dbservice.resource;

import com.techprimers.stock.dbservice.model.Quote;
import com.techprimers.stock.dbservice.model.Quotes;
import com.techprimers.stock.dbservice.repository.QuotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/db")
public class DbServiceResource {

    @Autowired
    private QuotesRepository quotesRepository;

    @GetMapping("/{username}")
    public List<String> getQuotes(@PathVariable("username") final String username) {

        return getQuotesByUsername(username);
    }

    private List<String> getQuotesByUsername(@PathVariable("username") String username) {
        return quotesRepository.findByUsername(username)
                .stream()
                .map(Quote::getQuote)
                .collect(Collectors.toList());
    }

    private List<Integer> getIdsByUsername(@PathVariable("username") String username) {
        return quotesRepository.findByUsername(username)
                .stream()
                .map(Quote::getId)
                .collect(Collectors.toList());
    }

    @PostMapping("/add")
    public List<String> add(@RequestBody final Quotes quotes) {
        quotes.getQuotes()
                .stream()
                .map(quote -> new Quote(quotes.getUsername(), quote))
                .forEach(quote -> {
                    quotesRepository.save(quote);
                });
        return getQuotesByUsername(quotes.getUsername());
    }

    @DeleteMapping("/delete/{username}")
    public List<String> delete(@PathVariable("username") final String username) {
        List<Quote> quotes = quotesRepository.findByUsername(username);
        quotesRepository.deleteAll(quotes);
        return getQuotesByUsername(username);
    }

    @PutMapping("/update/{username}")
    public List<String> update(@PathVariable("username") final String username, @RequestBody final Quotes quotes) {
        List<Integer> ids = getIdsByUsername(username);
        List<String> listQuotes = quotes.getQuotes();
        for (int i = 0; i < ids.size(); i++) {
            quotesRepository.save(new Quote(ids.get(i), username, listQuotes.get(i)));
        }
        return getQuotesByUsername(username);
    }
}
