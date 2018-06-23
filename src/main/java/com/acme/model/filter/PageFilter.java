//package com.acme.model.filter;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//@Getter
//@Setter
//public class PageFilter {
//
//  private int page;
//  private int size;
//  @JsonIgnore
//  private Sort sort;
//
//  public void readFromSource(Pageable pageable){
//    setPage(pageable.getPageNumber());
//    setSize(pageable.getPageSize());
//    setSort(pageable.getSort());
//  }
//
//}
