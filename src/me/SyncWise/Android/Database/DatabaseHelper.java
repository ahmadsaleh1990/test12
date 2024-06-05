package me.SyncWise.Android.Database;


import android.content.Context;
import android.database.Cursor;


public class DatabaseHelper {

    Context context;

    public DatabaseHelper(Context context) {
        this.context = context;
    }


    public void AddField(String tableName, String fieldName, String fieldType) {
       try{
           try{
               String Select = "SELECT "+fieldName+" FROM " + tableName;
               String selectionArguments[] = new String[]{};
               Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
           }
           catch (Exception e){
               String Query = "ALTER TABLE " + tableName + " ADD " + fieldName + " " + fieldType;
               String selectionArguments[] = new String[]{};
               DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
           }
       }
       catch(Exception e){}

    }
    public void CreateTableSOSTargetAssignment (  ) {
        try{
            try{
                String Select = "SELECT * FROM SOSTargetAssignment"  ;
                String selectionArguments[] = new String[]{};
                Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//               
//                DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
            }
            catch (Exception e){
                String Query =	"CREATE TABLE  iF NOT EXISTS  SOSTargetAssignment  (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'AssignmentCode' TEXT," + // 1: AssignmentCode
                "'TargetCode' TEXT," + // 2: TargetCode
                "'CompanyCode' TEXT," + // 3: CompanyCode
                "'AssignmentType' INTEGER," + // 4: AssignmentType
                "'StampDate' INTEGER NOT NULL )  "    ;
                String selectionArguments[] = new String[]{};
                DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
            }
        }
        catch(Exception e){
        	String a;
        }

     }
    public void CreateTableSOSCategoryBrands (  ) {
        try{
            try{
                String Select = "SELECT * FROM SOSCategoryBrands"  ;
                String selectionArguments[] = new String[]{};
                Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//               
//                DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
            }
            catch (Exception e){
                String Query =	" CREATE TABLE  iF NOT EXISTS   SOSCategoryBrands ( " +
                		 "'_id' INTEGER PRIMARY KEY ," + // 0: id
                         "'BrandCode' TEXT," + // 1: BrandCode
                         "'CompanyCode' TEXT," + // 2: CompanyCode
                         "'CategoryCode' TEXT," + // 3: CategoryCode
                         "'StampDate' INTEGER NOT NULL );"    ;
                String selectionArguments[] = new String[]{};
                DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
            }
        }
        catch(Exception e){
        	String a;
        }

     }
    public void CreateTableSOSBrands (  ) {
        try{
            try{
                String Select = "SELECT * FROM SOSBrands"  ;
                String selectionArguments[] = new String[]{};
                Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
             }
            catch (Exception e){
                String Query =	" CREATE TABLE  iF NOT EXISTS   SOSBrands (" +
                		   "'_id' INTEGER PRIMARY KEY ," + // 0: id
                           "'BrandCode' TEXT," + // 1: BrandCode
                           "'CompanyCode' TEXT," + // 2: CompanyCode
                           "'BrandDescription' TEXT," + // 3: BrandDescription
                           "'BrandAltDescription' TEXT," + // 4: BrandAltDescription
                           "'StampDate' INTEGER NOT NULL )"    ;
                String selectionArguments[] = new String[]{};
                DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
            }
        }
        catch(Exception e){
        	String a;
        }

     }
    
    public void CreateTableSOSCategories (  ) {
        try{
            try{
                String Select = "SELECT * FROM SOSCategories"  ;
                String selectionArguments[] = new String[]{};
                Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
             }
            catch (Exception e){
                String Query =	" CREATE TABLE  iF NOT EXISTS   SOSCategories (" +
                		  "'_id' INTEGER PRIMARY KEY ," + // 0: id
                          "'CategoryCode' TEXT," + // 1: CategoryCode
                          "'CompanyCode' TEXT," + // 2: CompanyCode
                          "'CategoryName' TEXT," + // 3: CategoryName
                        
                          "'StampDate' INTEGER NOT NULL );"    ;
                String selectionArguments[] = new String[]{};
                DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
            }
        }
        catch(Exception e){
        	String a;
        }

     }
    public void CreateTableSOSTrackerHeaders (  ) {
        try{
            try{
                String Select = "SELECT * FROM SOSTrackerHeaders"  ;
                String selectionArguments[] = new String[]{};
                Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
             }
            catch (Exception e){
                String Query =	" CREATE TABLE  iF NOT EXISTS   SOSTrackerHeaders (" +
                		 "'_id' INTEGER PRIMARY KEY ," + // 0: id
                         "'SOSCode' TEXT," + // 1: SOSCode
                         "'ClientCode ' TEXT," + // 2: ClientCode
                         "'UserCode' TEXT," + // 3: UserCode
                         "'VisitID' INTEGER," + // 4: VisitID
                         "'CompanyCode' TEXT," + // 5: CompanyCode
                         "'JounreyCode' TEXT," + // 6: JounreyCode
                         "'MeasurementType' INTEGER," + // 7: MeasurementType
                         "'IsProcessed' INTEGER NOT NULL ," + // 8: IsProcessed
                         "'StampDate' INTEGER NOT NULL );"; 
                String selectionArguments[] = new String[]{};
                DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
            }
        }
        catch(Exception e){
        	String a;
        }

     }
    public void CreateTableSOSTrackerDetails (  ) {
        try{
            try{
                String Select = "SELECT * FROM SOSTrackerDetails"  ;
                String selectionArguments[] = new String[]{};
                Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
             }
            catch (Exception e){
                String Query =	" CREATE TABLE  iF NOT EXISTS   SOSTrackerDetails (" +
                		   "'_id' INTEGER PRIMARY KEY ," + // 0: id
                           "'SOSCode' TEXT," + // 1: SOSCode
                           "'LineID' INTEGER," + // 2: LineID
                           "'SubjectCode' TEXT," + // 3: SubjectCode
                           "'SubjectType' INTEGER," + // 4: SubjectType
                           "'TargetValue' REAL," + // 5: TargetValue
                           "'ParentCode' TEXT," + // 6: ParentCode
                           "'OriginalTargetValue' REAL," + // 7: OriginalTargetValue
                           "'StampDate' INTEGER NOT NULL );"; 
                String selectionArguments[] = new String[]{};
                DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
            }
        }
        catch(Exception e){
        	String a;
        }

     }
     public void CreateTableSOSTargetDetails (  ) {
         try{
             try{
                 String Select = "SELECT * FROM SOSTargetDetails"  ;
                 String selectionArguments[] = new String[]{};
                 Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
              }
             catch (Exception e){
                 String Query =	" CREATE TABLE iF NOT EXISTS SOSTargetDetails  (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'TargetCode' TEXT," + // 1: TargetCode
                "'CompanyCode' TEXT," + // 2: CompanyCode
                "'SubjectCode' TEXT," + // 3: SubjectCode
                "'SubjectType' INTEGER," + // 4: SubjectType
                "'SubjectName' TEXT," + // 5: SubjectName
                "'TargetValue' REAL," + // 6: TargetValue
                "'ParentCode' TEXT," + // 7: ParentCode
                "'StampDate' INTEGER NOT NULL );"; 
                 String selectionArguments[] = new String[]{};
                 DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
             }
         }
         catch(Exception e){
         	String a;
         }}
         public void CreateTableSOSTargetHeaders (  ) {
             try{
                 try{
                     String Select = "SELECT * FROM SOSTargetHeaders"  ;
                     String selectionArguments[] = new String[]{};
                     Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
                  }
                 catch (Exception e){
                     String Query =	" CREATE TABLE iF NOT EXISTS SOSTargetHeaders  (" +                       
                    	                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                    	                "'TargetCode' TEXT," + // 1: TargetCode
                    	                "'CompanyCode' TEXT," + // 2: CompanyCode
                    	                "'TargetName' TEXT," + // 3: TargetName
                    	                "'TargetType' INTEGER," + // 4: TargetType
                    	                "'TargetNotes' TEXT," + // 5: TargetNotes
                    	                "'StampDate' INTEGER NOT NULL );"; 
                     String selectionArguments[] = new String[]{};
                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
             }
             catch(Exception e){
             	String a;
             }
      }
         
         
         public void CreateTableMSLDivisions (  ) {
             try{
                 try{
                     String Select = "SELECT * FROM MSLDivisions"  ;
                     String selectionArguments[] = new String[]{};
                     Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                     String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                    
//                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
                 catch (Exception e){
                     String Query =	" CREATE TABLE  iF NOT EXISTS   MSLDivisions (" +
                    		 "'_id' INTEGER PRIMARY KEY ," + // 0: id
                             "'DivisionCode' TEXT," + // 1: DivisionCode
                             "'CompanyCode' TEXT," + // 2: CompanyCode
                             "'StampDate' INTEGER NOT NULL );"    ;
                     String selectionArguments[] = new String[]{};
                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
             }
             catch(Exception e){
             	String a;
             }

          }
         public void CreateTableNewSkuListDivisions (  ) {
             try{
                 try{
                     String Select = "SELECT * FROM NewSkuListDivisions"  ;
                     String selectionArguments[] = new String[]{};
                     Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                     String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                    
//                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
                 catch (Exception e){
                     String Query =	" CREATE TABLE  iF NOT EXISTS   NewSkuListDivisions (" +
		                    		 "'_id' INTEGER PRIMARY KEY ," + // 0: id
		                             "'DivisionCode' TEXT," + // 1: DivisionCode
		                             "'CompanyCode' TEXT," + // 2: CompanyCode
		                             "'StampDate' INTEGER NOT NULL );"    ;
                     String selectionArguments[] = new String[]{};
                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
             }
             catch(Exception e){
             	String a;
             }

          }
          public void CreateTableTransactionDetailsMissedMSL (  ) {
              try{
                  try{
                      String Select = "SELECT * FROM TransactionDetailsMissedMSL"  ;
                      String selectionArguments[] = new String[]{};
                      Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                      String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                     
//                      DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                  }
                  catch (Exception e){
                      String Query =	" CREATE TABLE  iF NOT EXISTS   TransactionDetailsMissedMSL (" +
                    		  "'_id' INTEGER PRIMARY KEY ," + // 0: id
                              "'TransactionCode' TEXT NOT NULL ," + // 1: TransactionCode
                              "'LineID' INTEGER NOT NULL ," + // 2: LineID
                              "'ItemCode' TEXT," + // 3: ItemCode
                              "'OrderedType' TEXT," + // 4: OrderedType
                              "'QuantityBig' REAL," + // 5: QuantityBig
                              "'QuantityMedium' REAL," + // 6: QuantityMedium
                              "'QuantitySmall' REAL," + // 7: QuantitySmall
                              "'BasicUnitQuantity' REAL," + // 8: BasicUnitQuantity
                              "'ApprovedQuantityBig' REAL," + // 9: ApprovedQuantityBig
                              "'ApprovedQuantityMedium' REAL," + // 10: ApprovedQuantityMedium
                              "'ApprovedQuantitySmall' REAL," + // 11: ApprovedQuantitySmall
                              "'ApprovedBasicUnitQuantity' REAL," + // 12: ApprovedBasicUnitQuantity
                              "'MissedQuantityBig' REAL," + // 13: MissedQuantityBig
                              "'MissedQuantityMedium' REAL," + // 14: MissedQuantityMedium
                              "'MissedQuantitySmall' REAL," + // 15: MissedQuantitySmall
                              "'MissedBasicUnitQuantity' REAL," + // 16: MissedBasicUnitQuantity
                              "'PriceBig' REAL," + // 17: PriceBig
                              "'PriceMedium' REAL," + // 18: PriceMedium
                              "'PriceSmall' REAL," + // 19: PriceSmall
                              "'UserPriceBig' REAL," + // 20: UserPriceBig
                              "'UserPriceMedium' REAL," + // 21: UserPriceMedium
                              "'UserPriceSmall' REAL," + // 22: UserPriceSmall
                              "'DiscountPercentage' REAL," + // 23: DiscountPercentage
                              "'DiscountAmount' REAL," + // 24: DiscountAmount
                              "'TotalLineAmount' REAL," + // 25: TotalLineAmount
                              "'LineNote' TEXT," + // 26: LineNote
                              "'ItemLot' TEXT," + // 27: ItemLot
                              "'ReasonCode' TEXT," + // 28: ReasonCode
                              "'ItemTaxPercentage' REAL," + // 29: ItemTaxPercentage
                              "'ItemName' TEXT," + // 30: ItemName
                              "'ItemAltName' TEXT," + // 31: ItemAltName
                              "'ParentLineID' INTEGER," + // 32: ParentLineID
                              "'ItemExpiryDate' INTEGER," + // 33: ItemExpiryDate
                              "'ItemAffectedStock' TEXT," + // 34: ItemAffectedStock
                              "'IsInvoiceRelated' INTEGER," + // 35: IsInvoiceRelated
                              "'IsCompanyRelated' INTEGER," + // 36: IsCompanyRelated
                              "'TaxAmountBig' REAL," + // 37: TaxAmountBig
                              "'TaxAmountMedium' REAL," + // 38: TaxAmountMedium
                              "'TaxAmountSmall' REAL," + // 39: TaxAmountSmall
                              "'TotalExiceAmount' REAL," + // 40: TotalExiceAmount
                              "'ItemMSL' INTEGER," + // 41: ItemMSL
                              "'StampDate' INTEGER NOT NULL );"     ;
                      String selectionArguments[] = new String[]{};
                      DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                  }
              }
              catch(Exception e){
              	String a;
              }

           }
         public void CreateTableClientNewSkuList (  ) {
             try{
                 try{
                     String Select = "SELECT * FROM ClientNewSkuList"  ;
                     String selectionArguments[] = new String[]{};
                     Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                     String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                    
//                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
                 catch (Exception e){
                     String Query =	" CREATE TABLE  iF NOT EXISTS   ClientNewSkuList (" +
		                    		   "_id INTEGER PRIMARY KEY ," + // 0: id
		                    	       "SubjectCode TEXT NOT NULL ," + // 1: SubjectCode
		                               "ItemCode TEXT NOT NULL ," + // 2: ItemCode
		                               "DivisionCode TEXT NOT NULL ," + // 3: DivisionCode
		                               "CompanyCode TEXT NOT NULL ," + // 4: CompanyCode
		                               "Type INTEGER," + // 5: Type
		                               "StampDate INTEGER NOT NULL );"    ;
                     String selectionArguments[] = new String[]{};
                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
             }
             catch(Exception e){
             	String a;
             }

          }
         
         public void CreateTableDailyTargetAchievements (  ) {
             try{
                 try{
                     String Select = "SELECT * FROM DailyTargetAchievements"  ;
                     String selectionArguments[] = new String[]{};
                     Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                     String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                    
//                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
                 catch (Exception e){
                     String Query =	" CREATE TABLE  iF NOT EXISTS   DailyTargetAchievements (" +
                    		  "'_id' INTEGER PRIMARY KEY ," + // 0: id
                              "'TargetCode' TEXT," + // 1: TargetCode
                              "'TargetDescription' TEXT," + // 2: TargetDescription
                              "'VolumeMissed' INTEGER," + // 3: VolumeMissed
                              "'CalculatedMissed' INTEGER," + // 4: CalculatedMissed
                              "'VolumeNewSKU' INTEGER," + // 5: VolumeNewSKU
                              "'CalculatedNewSKU' INTEGER," + // 6: CalculatedNewSKU
                              "'ValidFrom' INTEGER," + // 7: ValidFrom
                              "'ValidTo' INTEGER," + // 8: ValidTo
                              "'StampDate' INTEGER NOT NULL );"      ;
                     String selectionArguments[] = new String[]{};
                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
             }
             catch(Exception e){
             	String a;
             }

          }
         public void CreateTableDuoUsers (  ) {
          try{
              try{
                  String Select = "SELECT * FROM DuoUsers"  ;
                  String selectionArguments[] = new String[]{};
                  Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                  String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                 
//                  DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
              }
              catch (Exception e){
                  String Query =	" CREATE TABLE  iF NOT EXISTS   DuoUsers (" +
                  		" '_id' INTEGER PRIMARY KEY ,  " +
                  		" 'UserCode' TEXT   ," +
                  		"   'DuoCode' TEXT ," +
                  		"   'CompanyCode' TEXT  ," +
                  		"	  'StampDate' INTEGER )"    ;
                  String selectionArguments[] = new String[]{};
                  DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
              }
          }
          catch(Exception e){
          	String a;
          }

       }
         public void CreateTableUserSpecialPriceLists (  ) {
             try{
                 try{
                     String Select = "SELECT * FROM UserSpecialPriceLists"  ;
                     String selectionArguments[] = new String[]{};
                     Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                     String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                    
//                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
                 catch (Exception e){
                     String Query =	" CREATE TABLE  iF NOT EXISTS   UserSpecialPriceLists (" +
                    		   "'_id' INTEGER PRIMARY KEY ," + // 0: id
                               "'UserCode' TEXT NOT NULL ," + // 1: UserCode
                               "'CompanyCode' TEXT," + // 2: CompanyCode
                               "'PriceListCode' TEXT," + // 3: PriceListCode
                               "'DivisionCode' TEXT," + // 4: DivisionCode
                               "'StampDate' INTEGER NOT NULL );"      ;
                     String selectionArguments[] = new String[]{};
                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
             }
             catch(Exception e){
             	String a;
             }

          }
         public void CreateTableForceSyncJourneys(  ) {
             try{
                 try{
                     String Select = "SELECT * FROM ForceSyncJourneys"  ;
                     String selectionArguments[] = new String[]{};
                     Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                     String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                    
//                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
                 catch (Exception e){
                     String Query =	" CREATE TABLE  iF NOT EXISTS   ForceSyncJourneys (" +
                   		  "'_id' INTEGER PRIMARY KEY ," + // 0: id
                             "'JourneyCode' TEXT NOT NULL UNIQUE ," + // 1: JourneyCode
                             "'UserCode' TEXT," + // 2: UserCode
                             "'SyncStatus' INTEGER," + // 3: SyncStatus
                             "'IsProcessed' INTEGER NOT NULL ," + // 4: IsProcessed
                             "'StampDate' INTEGER NOT NULL ); "      ;
                     String selectionArguments[] = new String[]{};
                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
             }
             catch(Exception e){
             	String a;
             }

          } 
         public void CreateTableUserVersions (  ) {
             try{
                 try{
                     String Select = "SELECT * FROM UserVersions"  ;
                     String selectionArguments[] = new String[]{};
                     Cursor c = DatabaseUtils.getInstance(context).getDaoSession().getDatabase().rawQuery(Select, selectionArguments);
//                     String Query =" DROP TABLE IF EXISTS ItemClassifications" ;
//                    
//                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
                 catch (Exception e){
                     String Query =	" CREATE TABLE  iF NOT EXISTS   UserVersions (" +
                     		 "'_id' INTEGER PRIMARY KEY ," + // 0: id
                              "'UserCode' TEXT NOT NULL ," + // 1: UserCode
                              "'CompanyCode' TEXT," + // 2: CompanyCode
                              "'VersionCode' TEXT," + // 3: VersionCode
                              "'IMEI' TEXT," + // 4: IMEI
                              "'StampDate' INTEGER NOT NULL ) ;"      ;
                     String selectionArguments[] = new String[]{};
                     DatabaseUtils.getInstance(context).getDaoSession().getDatabase().execSQL(Query, selectionArguments);
                 }
             }
             catch(Exception e){
             	String a;
             }

          }
}