package com.vcp.hessen.kurhessen.data;

import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;



@Service
@Slf4j
@RequiredArgsConstructor()
public class TribeService {
    private final TribeRepository tribeRepository;
    private final AuthenticatedUser authenticatedUser;


    public Set<String> getUserTags() {
        User user = authenticatedUser.get().orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        Set<Set<String>> tags = tribeRepository.getAllUserTags(user.getTribe());

        try {
            return tags.stream()
                    .flatMap(Set::stream)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new HashSet<>();
        }
    }
}
