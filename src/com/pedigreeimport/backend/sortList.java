package com.pedigreeimport.backend;

import java.util.*;
import java.io.*;

public class sortList{

	public List<String> algo(List<String> obj) throws IOException{
		int j=0;
		String s="";
		List<String> list = new ArrayList<String>();
		List<String> sortedList = new ArrayList<String>();

		list.clear();
		sortedList.clear();

		for(int i=0; i<obj.size();i++){
			if(j<=3){
				if(j==3){
					list.add(s+=""+obj.get(i)+";");
					s="";
					j=0;
				}else{
					j++;  
					s+=""+obj.get(i)+";";

				}
			}
		}
		
		for(int i=0; i<list.size();i++){
			////System.out.println("\n\n");
			////System.out.println("**********Before Sorting******");
			////System.out.println("index:"+i);
			////System.out.println("list["+i+"]:"+list.get(i));

			//*****get the contents of list and subdivide it
			String[] column = list.get(i).split(";");

			String crossed = column[0];  //*****crossed
			String female = column[1];  //******female
			String male = column[2];    //******male
			String date = column[3];
			//////System.out.println("List:"+list);

			////System.out.println("**********After sorting******");
			//***call sortingList function to sort the germplasm list
			sortedList = sortingList(female,male,list,i);
			////System.out.println("sorted List:"+sortedList);
		}
		////System.out.println("sorted List:"+sortedList.size());
		
		List<String> list2 = new ArrayList<String>();
		for(int i=0; i<sortedList.size();i++){
			String[] column = sortedList.get(i).split(";");
			list2.add(column[0]);
			list2.add(column[1]);
			list2.add(column[2]);
			list2.add(column[3]);
		}
		System.out.println("list:"+list2);
		
		
		return list2;
	}
	/*
	 * The function that sorts the germplasm list
	 * @param female --female parent
	 * @param male --male parent
	 * @param list -- germplasm list
	 * @param index -- index/position of the parents in the germplasm list
	 * */
	public static List<String> sortingList(String female, String male, List<String> list, int index){

		int femaleIndex, maleIndex;
		int fList_index2, mList_index2;

		List<String> femaleList = new ArrayList<String>();
		List<String> maleList = new ArrayList<String>();
		List<String> crossedList = new ArrayList<String>();
		List<String> crossedDateList = new ArrayList<String>();

		femaleList.clear();
		maleList.clear();
		crossedList.clear();
		crossedDateList.clear();


		crossedList = GetCrossedList(list);
		femaleList = GetFemaleParents(list);
		maleList = GetMaleParents(list);
		crossedDateList = GetCrossedDateList(list);

		/*////System.out.println("****BEFORE*****");
		    ////System.out.println("crossed:"+crossedList);
		    ////System.out.println("females:"+femaleList);
		    ////System.out.println("males:"+maleList);*/

		//***check if female exists in the crossed list
		while( (crossedList.contains(female) || (crossedList.contains(male)) ) && ( (femaleList.indexOf(female) < crossedList.indexOf(female)) || ((maleList.indexOf(male) < crossedList.indexOf(male))) ) ){
			
			
			
			if((femaleList.indexOf(female) < crossedList.indexOf(female)) ){
				//System.out.println("****FEMALE*****");
				//System.out.println("list:"+list);
	            //System.out.println("female:"+female) ;
	            
				femaleIndex = crossedList.indexOf(female);
				fList_index2 = femaleList.indexOf(female);
				index = fList_index2;
				
				//System.out.println("femaleList index:"+femaleList.indexOf(female));
				//System.out.println("crossedList index:"+crossedList.indexOf(female));
				
				Collections.swap(list, fList_index2, femaleIndex);

				// sortingList(female_1,male_1,list,index);
				crossedList = GetCrossedList(list);
				femaleList = GetFemaleParents(list);
				maleList = GetMaleParents(list);
				crossedDateList = GetCrossedDateList(list);
				
				female = femaleList.get(index);
				male = maleList.get(index);
              
				//System.out.println("new list:"+list);
				/*//System.out.println("****HERE*****");
					    //System.out.println("crossed:"+crossedList);
					    //System.out.println("females:"+femaleList);*/
				//System.out.println("index here F:"+index);
				//System.out.println("female:"+female);
				 //System.out.println("male:"+male) ;
				//System.out.println("crossed:"+crossedList);
				//System.out.println("females:"+femaleList);
				//System.out.println("males:"+maleList);

				//System.out.println("\n");
			}


			/*//System.out.println("****AFTER*****");*/

		

			if((maleList.indexOf(male) < crossedList.indexOf(male)) ){

				//System.out.println("****MALE*****");
				//System.out.println("list:"+list);
				
				int f_1 = crossedList.indexOf(female);
				maleIndex = crossedList.indexOf(male);
				mList_index2 = maleList.indexOf(male);
				index = mList_index2;
				//System.out.println("index here M:"+index);
			
				//System.out.println("maleList index:"+maleList.indexOf(male));
				//System.out.println("crossedList index:"+crossedList.indexOf(male));
				//System.out.println("list receive:"+list);
				/*//System.out.println("female receive:"+female);
				 */
				Collections.swap(list, mList_index2, maleIndex);

				// sortingList(female_1,male_1,list,index);

				crossedList = GetCrossedList(list);
				femaleList = GetFemaleParents(list);
				maleList = GetMaleParents(list);
				crossedDateList = GetCrossedList(list);

				male = maleList.get(index);
				female = femaleList.get(index);
				
				//System.out.println("female:"+female);
			    //System.out.println("male:"+male) ;
			    
				//System.out.println("crossed:"+crossedList);
				//System.out.println("females:"+femaleList);
				//System.out.println("males:"+maleList);

				//System.out.println("new list:"+list);
				//System.out.println("\n");
				/*//System.out.println("****HERE*****");
					    //System.out.println("crossed:"+crossedList);
					    //System.out.println("females:"+femaleList);*/
			}

		}
		//System.out.println("li:"+list);
		return list;
	}

	public static List<String> GetFemaleParents(List<String> list){

		List<String> femaleList = new ArrayList<String>();
		femaleList.clear();


		for(int j=0;j<list.size();j++){
			String[] column = list.get(j).split(";");
			femaleList.add(column[1]);	 
		}

		return femaleList;
	}

	public static List<String> GetMaleParents(List<String> list){

		List<String> maleList = new ArrayList<String>();
		maleList.clear();

		for(int j=0;j<list.size();j++){
			String[] column = list.get(j).split(";");
			maleList.add(column[2]);	 
		}

		return maleList;
	}

	public static List<String> GetCrossedList(List<String> list){

		List<String>crossedList = new ArrayList<String>();
        crossedList.clear();
        
		for(int i=0;i<list.size();i++){
			//System.out.println("date: "+list.get(i));
			String[] column = list.get(i).split(";");
			crossedList.add(column[0]);
		}
		return crossedList;
	}
	
	public static List<String> GetCrossedDateList(List<String> list){
		
		
		List<String>crossedDateList = new ArrayList<String>();
		crossedDateList.clear();
		//System.out.println("HERE!");
		for(int i=0;i<list.size();i++){
			//System.out.println("date: "+list.get(i));
			String[] column = list.get(i).split(";");
			
		  	crossedDateList.add(column[3]);
		  	
		}
		
		return crossedDateList;
	}


}