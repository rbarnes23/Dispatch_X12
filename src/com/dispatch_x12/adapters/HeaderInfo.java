package com.dispatch_x12.adapters;

import java.util.ArrayList;
 
public class HeaderInfo {
  
 private CharSequence name;
 private ArrayList<DetailInfo> detailList = new ArrayList<DetailInfo>();
  
 public CharSequence getName() {
  return name;
 }
 public void setName(CharSequence name) {
  this.name = name;
 }
 public ArrayList<DetailInfo> getDetailList() {
  return detailList;
 }
 public void setDetailList(ArrayList<DetailInfo> detailList) {
  this.detailList = detailList;
 }
 
}