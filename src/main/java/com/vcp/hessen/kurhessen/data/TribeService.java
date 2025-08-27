package com.vcp.hessen.kurhessen.data;

import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;



@Service
@Slf4j
@RequiredArgsConstructor()
public class TribeService {
    private final TribeRepository tribeRepository;
    private final AuthenticatedUser authenticatedUser;
    private final UserTagRepository userTagRepository;


    public Set<UserTag> getUserTags() {
        User user = authenticatedUser.get().orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        Set<UserTag> tags = tribeRepository.getAllUserTags(user.getTribe());

        try {
            return tags.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new HashSet<>();
        }
    }

    public UserTag addUserTag(UserTag userTag) {
        User user = authenticatedUser.get().orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));
        userTag.setTribe(user.getTribe());
        return userTagRepository.save(userTag);

    }

    public Page<Tribe> list(Pageable pageable) {
        return tribeRepository.findAll(pageable);
    }

    public List<Tribe> findAll() {
        return tribeRepository.findAll();
    }

}
