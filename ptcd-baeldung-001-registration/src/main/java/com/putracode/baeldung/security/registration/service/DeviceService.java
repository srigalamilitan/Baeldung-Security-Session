package com.putracode.baeldung.security.registration.service;

import com.google.common.base.Strings;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.putracode.baeldung.security.registration.persistence.dao.DeviceMetadataRepository;
import com.putracode.baeldung.security.registration.persistence.model.DeviceMetadata;
import com.putracode.baeldung.security.registration.persistence.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class DeviceService {
    private final Logger logger=LoggerFactory.getLogger(getClass());

    private static final String UNKONWN="UNKOWN";

    @Value("${support.email}")
    private String from;

    private DeviceMetadataRepository deviceMetadataRepository;
    private DatabaseReader databaseReader;
    private Parser parser;
    private JavaMailSender javaMailSender;
    private MessageSource messages;

    public DeviceService(DeviceMetadataRepository deviceMetadataRepository,
                         DatabaseReader databaseReader,
                         Parser parser,
                         JavaMailSender mailSender,
                         MessageSource messages) {
        this.deviceMetadataRepository = deviceMetadataRepository;
        this.databaseReader = databaseReader;
        this.parser = parser;
        this.javaMailSender = mailSender;
        this.messages = messages;
    }
    public void verifyDevice(User user,HttpServletRequest request)throws IOException, GeoIp2Exception{
        String ip=extractIp(request);
        String location=getIpLocation(ip);
        String deviceDetial =getDeviceDetails(request.getHeader("user-agent"));
        DeviceMetadata existingDevice=findExistingDevice(user.getId(),deviceDetial,location);
        if(isNull(existingDevice)){
            unknownDeviceNotification(deviceDetial,location,ip,user.getEmail(),request.getLocale());
            DeviceMetadata deviceMetadata=new DeviceMetadata();
            deviceMetadata.setUserId(user.getId());
            deviceMetadata.setLocation(location);
            deviceMetadata.setDeviceDetails(deviceDetial);
            deviceMetadata.setLastLoggedIn(new Date());
            deviceMetadataRepository.save(deviceMetadata);
        }else{
            existingDevice.setLastLoggedIn(new Date());
            deviceMetadataRepository.save(existingDevice);
        }

    }
    private String getDeviceDetails(String userAgent){
        String deviceDetails=UNKONWN;
        Client client=parser.parse(userAgent);
        if(nonNull(client)){
            deviceDetails=client.userAgent.family+" "+client.userAgent.major+"."+client.userAgent.minor+
                    " - "+ client.os.family+" "+client.os.major+"."+client.os.minor;
        }
        return  deviceDetails;
    }
    private void unknownDeviceNotification(String deviceDetails, String location, String ip, String email, Locale locale){
        final String subject ="New Login Notification";
        final SimpleMailMessage notification=new SimpleMailMessage();
        notification.setTo(email);
        notification.setSubject(subject);
        String text= messages.getMessage("message.login.notification.deviceDetails",null,locale)+" "+deviceDetails+
                "\n"+messages.getMessage("message.login.notification.location",null,locale)+" "+location+
                "\n"+messages.getMessage("message.login.notification.ip",null,locale)+" "+ip;
        notification.setText(text);
        notification.setFrom(from);
        javaMailSender.send(notification);
    }
    private DeviceMetadata findExistingDevice(Long userId, String deviceDetails, String location){
        List<DeviceMetadata> knownDevices=deviceMetadataRepository.findByUserId(userId);
        for(DeviceMetadata existingDevice: knownDevices){
            if(existingDevice.getDeviceDetails().equalsIgnoreCase(deviceDetails) && existingDevice.getLocation().equalsIgnoreCase(location)){
                return existingDevice;
            }
        }
        return null;
    }
    private String getIpLocation(String ip) throws IOException,GeoIp2Exception {
        String location=UNKONWN;
        InetAddress ipAddress=InetAddress.getByName(ip);
        CityResponse cityResponse=databaseReader.city(ipAddress);
        if(nonNull(cityResponse) && nonNull(cityResponse.getCity()) && !Strings.isNullOrEmpty(cityResponse.getCity().getName())){
            location=cityResponse.getCity().getName();
        }
        return location;
    }
    private String extractIp(HttpServletRequest request){
        String clientIp;
        String clientXForwardedForIp=request.getHeader("x-forwarded-for");
        if(nonNull(clientXForwardedForIp)){
            clientIp=parseXForwardedHeader(clientXForwardedForIp);
        }else
        {
            clientIp=request.getRemoteAddr();
        }
        return clientIp;
    }
    private String parseXForwardedHeader(String header){
        return header.split("*, *")[0];
    }

}
