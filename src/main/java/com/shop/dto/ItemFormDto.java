package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {  // 프론트에서 넘어오는 객체 처리용

    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();    // 상품 저장 후 수정할 때 상품 이미지 정보를 저장하는 리스트

    private List<Long> itemImgIds = new ArrayList<>();  // 상품의 이미지 아이디를 저장하는 리스트.

    // 232 추가 모델 처리를 위한 라이브러리 (DTO와 엔티티간의 변환 처리용) -> config.RootConfig에 적용
    // 상품을 등록할 때 화면으로 부터 전달 받은 DTO 객체를 엔티티로 변환하는 작업 대체(DTOtoEntity, EntityToDTO)
    // 서로다른 클래스의 값을 필드의 이름과 자료형이 같으면 getter, setter를 통해 값을 복사해서 객체로 변환 해줌)
    //    implementation 'org.modelmapper:modelmapper:3.1.0'
    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){   // 엔티티 객체와 DTO 객체 간의 데이터를 복사하여 반환해주는 메서드
        return modelMapper.map(this, Item.class);
    }

    public static ItemFormDto of(Item item){    // 엔티티 객체와 DTO 객체 간의 데이터를 복사하여 반환해주는 메서드
        return modelMapper.map(item, ItemFormDto.class);
    }
}
