package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")    // application.properties에 등록한 "#상품 이미지 업로드 경로 itemImgLocation=C:/shop/item" 불러옴
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    // 상품 이미지 저장
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 사용자가 상품의 이미지를 등록했다면 저장할 경로와 파일의 이름, 파일의 바이트 배열을 파일 업로드 파라미터로 uploadFile 메서드 호출
            imgUrl = "/images/item/" + imgName;
            // 저장한 상품의 이미지 불러올 경로를 설정 WebMvcConfig 에서 설정함 c:\shop이므로 /images/item/를 붙여줌
        }

        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        // imgName : 실제 로컬에 저장된 상품 이미지 파일의 이름, oriImgName : 업로드했던 상품 이미지 파일의 원래 이름, imgUrl : 업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
        itemImgRepository.save(itemImg);    // 입력받은 상품 이미지 정보를 저장
    }

    // 상품 이미지 수정
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){ // 상품 이미지를 수정한 경우 상품 이미지 업데이트
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)    // 아이디를 이용하여 기존에 저장했던 상품 이미지 엔티티 조회
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())) {   // 기존에 등록된 상품 이미지 파일이 있을 경우 해당 파일 삭제
                fileService.deleteFile(itemImgLocation+"/"+
                        savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);    // 업데이트한 상품 이미지 파일을 업로드
            // itemImgRepository.save() 로직을 호출하지 않음. savedItemImg 엔티티는 현재 영속 상태이므로 데이터를 변경하는 것만으로
            // 변경 감지 기능이 동작하여 트랜잭션이 끝날 때 update 쿼리가 실행된다. (엔티티가 영속 상태여야 함)
        }
    }
}
