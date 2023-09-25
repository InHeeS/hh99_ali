//package com.example.ali.security;
//
//import com.example.ali.entity.Seller;
//import com.example.ali.repository.SellerRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class SellerDetailsServiceImpl implements UserDetailsService {
//
//    private final SellerRepository sellerRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Seller seller = sellerRepository.findByUsername(username)
//            .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));
//
//        return new SellerDetailsImpl(seller);
//    }
//
//}
