package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;    // 아이템 서비스
    private final ItemImgService itemImgService;    // 아이템 이미지 서비스
    private final ItemImgRepository itemImgRepository;  // 이미지 db 연동

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        // 상품 등록
        Item item = itemFormDto.createItem();   // 등록 폼으로 입력 받은 데이터를 이용해 객체 생성
        itemRepository.save(item);              // db에 저장

        // 이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i==0)
                itemImg.setRepimgYn("Y");       // 이미지가 첫번째 일 경우 대표이미지 Y 처리
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));    // 상품 이미지 저장
        }
        return item.getId();                    // 저장된 id를 리턴
    }

    // 상품 조회
    @Transactional(readOnly = true) // 상품을 읽어오는 트랜젝션을 읽기 전용으로 설정하면 성능이 개선됨.(더티체킹(변경감지) 수행않음)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId); // 해당 상품의 이미지 조회 (오름차순)
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {   // 조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId) // 상품의 아이디를 통해 상품 엔티티를 조회
                .orElseThrow(EntityNotFoundException::new); // 존재하지 않을 때는 EntityNotFoundException 발생
        ItemFormDto itemFormDto = ItemFormDto.of(item); // 상품 엔티티를 dto 객체로 변환
        itemFormDto.setItemImgDtoList(itemImgDtoList);  // 해당 상품의 이미지 리스트를 dto 객체에 넣음
        return itemFormDto;
    }

    //상품 수정
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())    // 상품 등록 화면으로부터 전달 받은 상품 아이디를 이용하여 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);   // 상품 등록 화면으로부터 전달 받은 ItemFormDto를 통해 상품 엔티티를 업데이트
        
        List<Long> itemImgIds = itemFormDto.getItemImgIds();    // 상품 이미지 아이디 리스트를 조회

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));    // 상품 이미지 업데이트 위해 updateItemImg() 메서드에 상품 이미지 아이디와 상품 이미지 파일 정보를 파라미터로 전달
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    } // 페이지 처리되는 아이템 처리용 (상품 조회 조건과 페이지 정보를 파라미터로 받아서 상품 데이터를 조회)

    @Transactional(readOnly = true) // 메인 페이지용 서비스
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

}
