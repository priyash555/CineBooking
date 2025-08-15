package com.kaankaplan.userService.business.concretes;

import com.kaankaplan.userService.business.abstracts.ClaimService;
import com.kaankaplan.userService.business.abstracts.UserService;
import com.kaankaplan.userService.dao.ClaimDao;
import com.kaankaplan.userService.dao.UserDao;
import com.kaankaplan.userService.entity.Claim;
import com.kaankaplan.userService.entity.User;
import com.kaankaplan.userService.entity.dto.UserRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ClaimDao claimDao;
    private final ClaimService claimService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Boolean isUserExist(String userId) {

        User user = userDao.findUserByUserId(userId);

        if (user == null) {
            return false;
        }

        return true;
    }

    @Override
    public void addUser(UserRegisterRequestDto userRegisterRequestDto) {

        Claim adminClaim = Claim.builder().claimId("1").claimName("ADMIN").build();
        Claim cusClaim = Claim.builder().claimId("2").claimName("CUSTOMER").build();
        claimDao.insert(adminClaim);
        claimDao.insert(cusClaim);

        Claim claim = claimService.getClaimByClaimName("ADMIN");

        User user = User.builder()
                        .email(userRegisterRequestDto.getEmail())
                        .password(passwordEncoder.encode(userRegisterRequestDto.getPassword()))
                        .fullName(userRegisterRequestDto.getCustomerName())
                        .claim(claim)
                        .build();
        System.out.println(user);
        userDao.insert(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    public boolean isUserCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().allMatch(
                a -> a.getAuthority().equals("ROLE_CUSTOMER")
        )) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN")
        )) {
            return true;
        }
        return false;
    }
}
