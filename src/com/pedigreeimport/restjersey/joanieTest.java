package com.pedigreeimport.restjersey;

import java.text.ParseException;
import java.util.*;
import java.io.*;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public class joanieTest{


	public static void main(String[] args) throws IOException, MiddlewareQueryException, ParseException{
		sortList();
	}
	public static void sortList() throws IOException{

		String csvFile = "E:/Installed Software/XAMPP/htdocs/GMGR/csv_files/sampleList.csv";

		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> sortedList = new ArrayList<String>();

		list.clear();
		sortedList.clear();

		FileReader sampleFile = new FileReader(csvFile);
		BufferedReader br = new BufferedReader(sampleFile);
		String line;

		while((line = br.readLine()) != null){
			System.out.println("***"+line);
			list.add(line);
		}

		System.out.println("list:"+list);

		for(int i=0; i<list.size();i++){
			System.out.println("\n\n");
			System.out.println("**********Before Sorting******");
			System.out.println("index:"+i);
			System.out.println("list["+i+"]:"+list.get(i));

			//*****get the contents of list and subdivide it
			String[] column = list.get(i).split(";");

			String crossed = column[0];  //*****crossed
			String female = column[1];  //******female
			String male = column[2];    //******male
			//System.out.println("List:"+list);

			System.out.println("**********After sorting******");
			//***call sortingList function to sort the germplasm list
			sortedList = sortingList(female,male,list,i);
			System.out.println("sorted List:"+sortedList);


		}


		//closes file                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
		sampleFile.close();
		br.close();
	}
	/*
	 * The function that sorts the germplasm list
	 * @param female --female parent
	 * @param male --male parent
	 * @param list -- germplasm list
	 * @param index -- index/position of the parents in the germplasm list
	 * */
	public static ArrayList<String> sortingList(String female, String male, ArrayList<String> list, int index){

		int femaleIndex, maleIndex;
		int fList_index2, mList_index2;

		ArrayList<String> femaleList = new ArrayList<String>();
		ArrayList<String> maleList = new ArrayList<String>();
		ArrayList<String> crossedList = new ArrayList<String>();

		femaleList.clear();
		maleList.clear();
		crossedList.clear();


		crossedList = GetCrossedList(list);
		femaleList = GetFemaleParents(list);
		maleList = GetMaleParents(list);

		/*System.out.println("****BEFORE*****");
		    System.out.println("crossed:"+crossedList);
		    System.out.println("females:"+femaleList);
		    System.out.println("males:"+maleList);*/

		//***check if female exists in the crossed list
		while( (crossedList.contains(female) || (crossedList.contains(male)) ) && ( (femaleList.indexOf(female) < crossedList.indexOf(female)) || ((maleList.indexOf(male) < crossedList.indexOf(male))) ) ){
		
			if((femaleList.indexOf(female) < crossedList.indexOf(female)) ){
				System.out.println("****FEMALE*****");
				System.out.println("list:"+list);
	            System.out.println("female:"+female) ;
	            
				femaleIndex = crossedList.indexOf(female);
				fList_index2 = femaleList.indexOf(female);
				index = fList_index2;
				
				System.out.println("femaleList index:"+femaleList.indexOf(female));
				System.out.println("crossedList index:"+crossedList.indexOf(female));
				
				Collections.swap(list, fList_index2, femaleIndex);

				// sortingList(female_1,male_1,list,index);
				crossedList = GetCrossedList(list);
				femaleList = GetFemaleParents(list);
				maleList = GetMaleParents(list);
				
				female = femaleList.get(index);
				male = maleList.get(index);
              
				System.out.println("new list:"+list);
				/*System.out.println("****HERE*****");
					    System.out.println("crossed:"+crossedList);
					    System.out.println("females:"+femaleList);*/
				System.out.println("index here F:"+index);
				System.out.println("female:"+female);
				 System.out.println("male:"+male) ;
				System.out.println("crossed:"+crossedList);
				System.out.println("females:"+femaleList);
				System.out.println("males:"+maleList);

				System.out.println("\n");
			}


			/*System.out.println("****AFTER*****");*/

		

			if((maleList.indexOf(male) < crossedList.indexOf(male)) ){

				System.out.println("****MALE*****");
				System.out.println("list:"+list);
				
				int f_1 = crossedList.indexOf(female);
				maleIndex = crossedList.indexOf(male);
				mList_index2 = maleList.indexOf(male);
				index = mList_index2;
				System.out.println("index here M:"+index);
			
				System.out.println("maleList index:"+maleList.indexOf(male));
				System.out.println("crossedList index:"+crossedList.indexOf(male));
				System.out.println("list receive:"+list);
				/*System.out.println("female receive:"+female);
				 */
				Collections.swap(list, mList_index2, maleIndex);

				// sortingList(female_1,male_1,list,index);

				crossedList = GetCrossedList(list);
				femaleList = GetFemaleParents(list);
				maleList = GetMaleParents(list);

				male = maleList.get(index);
				female = femaleList.get(index);
				
				System.out.println("female:"+female);
			    System.out.println("male:"+male) ;
			    
				System.out.println("crossed:"+crossedList);
				System.out.println("females:"+femaleList);
				System.out.println("males:"+maleList);

				System.out.println("new list:"+list);
				System.out.println("\n");
				/*System.out.println("****HERE*****");
					    System.out.println("crossed:"+crossedList);
					    System.out.println("females:"+femaleList);*/
			}

		}

		//****check the male parent
		/*while( crossedList.contains(male) &&  (maleList.indexOf(male) < crossedList.indexOf(male)) ) {

			System.out.println("****MALE*****");
			System.out.println("list:"+list);
			if((maleList.indexOf(male) < crossedList.indexOf(male)) ){

				int f_1 = crossedList.indexOf(female);
				maleIndex = crossedList.indexOf(male);
				int mList_index2 = maleList.indexOf(male);
				index = mList_index2;
				System.out.println("maleList index:"+maleList.indexOf(male));
				System.out.println("crossedList index:"+crossedList.indexOf(male));
				System.out.println("list receive:"+list);
				/*System.out.println("female receive:"+female);
				 */
		/*		Collections.swap(list, mList_index2, maleIndex);

				// sortingList(female_1,male_1,list,index);

				crossedList = GetCrossedList(list);
				femaleList = GetFemaleParents(list);
				maleList = GetMaleParents(list);

				male = maleList.get(index);
				System.out.println("male:"+male);
				System.out.println("crossed:"+crossedList);
				System.out.println("females:"+femaleList);
				System.out.println("males:"+maleList);

				System.out.println("new list:"+list);
				/*System.out.println("****HERE*****");
					    System.out.println("crossed:"+crossedList);
					    System.out.println("females:"+femaleList);*/
			/*}else{
				//do nothing
			}

			if( (femaleList.indexOf(female)) < (crossedList.indexOf(female))){
				sortingList(female, male, crossedList, f_1);
			}
		}*/
		return list;
	}

	public static ArrayList<String> GetFemaleParents(ArrayList<String> list){

		ArrayList<String> femaleList = new ArrayList<String>();
		femaleList.clear();


		for(int j=0;j<list.size();j++){
			String[] column = list.get(j).split(";");
			femaleList.add(column[1]);	 
		}

		return femaleList;
	}

	public static ArrayList<String> GetMaleParents(ArrayList<String> list){

		ArrayList<String> maleList = new ArrayList<String>();
		maleList.clear();

		for(int j=0;j<list.size();j++){
			String[] column = list.get(j).split(";");
			maleList.add(column[2]);	 
		}

		return maleList;
	}

	public static ArrayList<String> GetCrossedList(ArrayList<String> list){

		ArrayList<String>crossedList = new ArrayList<String>();

		for(int i=0;i<list.size();i++){
			String[] column = list.get(i).split(";");
			crossedList.add(column[0]);
		}
		return crossedList;
	}


}