package com.lnu.foundation.controller;

import com.lnu.foundation.model.Note;
import com.lnu.foundation.model.TestSession;
import com.lnu.foundation.model.Therapy;
import com.lnu.foundation.model.User;
import com.lnu.foundation.model.youtube.Item;
import com.lnu.foundation.model.youtube.Videos;
import com.lnu.foundation.service.NoteService;
import com.lnu.foundation.service.SecurityContextService;
import com.lnu.foundation.service.UserService;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by kangul on 10/10/2019.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static String YOUTUBE_PARKINSONS_VIDEO = "https://www.googleapis.com/youtube/v3/search?part=id%2Csnippet&maxResults=10&order=date&q=parkinson%20diseasee%20exercises&prettyPrint=true&key=AIzaSyCFw-l8-axsDFYTzAq5tIc2RixHW29vq18";

    @Autowired
    private UserService service;
    @Autowired
    private SecurityContextService securityContextService;

    @Autowired
    private NoteService noteService;

    @Autowired
    RestTemplate restTemplate;

    @CrossOrigin(origins = {"http://localhost:4200"})
    @GetMapping("/me/tests")
    public Collection<TestSession> getTestSessions() {
        Collection<TestSession> sessions;
        User user = securityContextService.currentUser().orElseThrow(RuntimeException::new);
        if (user.getRole() != null && "researcher".equals(user.getRole().getName())) {
            sessions = service.getSessions(user.getUsername());
        } else {
            sessions = service.getSessions();
        }
        return sessions;
    }

    @CrossOrigin(origins = {"http://localhost:4200"})
    @GetMapping("user/{username}/tests")
    public Collection<TestSession> getPatientTestSessions(@PathVariable String username) {
        return service.getPatientSessions(username);
    }

    @CrossOrigin(origins = {"http://localhost:4200"})
    @GetMapping("user/{username}")
    public User getUser(@PathVariable String username) {
        return service.findUserByUsername(username);
    }

    @CrossOrigin(origins = {"http://localhost:4200"})
    @GetMapping("/me")
    public User getMe() {
        return securityContextService.currentUser().orElseThrow(RuntimeException::new);
    }

    @CrossOrigin(origins = {"http://localhost:4200"})
    @PostMapping("user/me/tests/{testSessionId}/note")
    public Collection<Note> addNote(@PathVariable Long testSessionId, @RequestBody Note note) {
        User user = securityContextService.currentUser().orElseThrow(RuntimeException::new);
        return noteService.addNote(testSessionId, note, user);
    }


    @CrossOrigin(origins = {"http://localhost:4200"})
    @GetMapping("/rssfeed")
    private List<SyndEntry> getRSSFeed() {
        SyndFeed feed = null;
        try {
            String url = "https://www.news-medical.net/tag/feed/parkinsons-disease.aspx";
            try (XmlReader reader = new XmlReader(new URL(url))) {
                feed = new SyndFeedInput().build(reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed.getEntries();
    }

    @CrossOrigin(origins = {"http://localhost:4200"})
    @GetMapping("/youtubeVideos")
    private List<Item> getYoutubeVideos() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<Videos> response = restTemplate.exchange(URLDecoder.decode(YOUTUBE_PARKINSONS_VIDEO), HttpMethod.GET, entity, new ParameterizedTypeReference<Videos>() {
        });
        Videos videos = response.getBody();
        return videos.getItems();
    }

    @CrossOrigin(origins = {"http://localhost:4200"})
    @GetMapping("/therapies")
    public Collection<Therapy> getTherapies() {
        Collection<Therapy> therapies = null;
        User user = securityContextService.currentUser().orElseThrow(RuntimeException::new);
        if ("physician".equals(user.getRole().getName())) {
            therapies = service.getTherapiesByMed(user.getUsername());
        } else if ("researcher".equals(user.getRole().getName())
                || "junior researcher".equals(user.getRole().getName())) {
            therapies = service.getTherapies();
        } else {
            therapies = service.getTherapiesByPatient(user.getUsername());
        }

        return therapies;
    }

}
