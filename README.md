# EatClub Challenge

This project solves `EatClub Tech Challenge - Java AWS - v2`.

## Requirements

- Java 21 (currentl LTS & latest AWS Lambda Java runtime version at the time of writing)

## Build and Run

To build and run the application once cloned to a directory of your choice, you can use the following commands:

```shell
./mvnw clean package
java -jar target/eatclub-1.0.0.jar
```

then to access the API,

```shell
curl '127.0.0.1:8080/api/deals?timeOfDay=15:00'
curl '127.0.0.1:8080/api/peak'
```

## Notes, Observations & Assumptions

- The eccdn data has ID clashes on Deal ID `B5713CD0-1111-40C7-AFC3-7D46D26B00BF`, assumed to be a data generation issue not a real-world hash collision
- Deals are seen with either start and end or open & close fields. This is handled as if the data model changed over time, but is assumed to be a GenAI hallucination in the demo data.
- Only 'lightening' deals have time overrides. Non-lightening deals are assumed to be all day deals, where all day is the opening hours of the restaurant.
- Active does not mean available to use i.e an active deal may have 0 quantity available if all deals have been redeemed. (This was seedn in real-world eatclub app use)
- If a deal has both start/end and open/close times, the start/end times are used.
- No restaurants are open over midnight, but real world data may have 24 hour or late-night restaurants so the code handles midnight crossings

### Task 1

- The Task 1 response format has a typo "restarantSuburb" instead of "restaurantSuburb". This is intentionally kept in the code as it is specified in the task, and may be part of an automated response check however in a real-world scenario the typo would be clarified and corrected at requirement gathering time.
- All fields in the response are specified as Strings, but the content/format is not provided. The solution follows the format of the eccdn example data e.g times are formatted as "3:00pm" vs "15:00"
- The Deal response format doesnt include the deal time, so callers might mistake the deal time for the restaurant opening time.

### Task 2

- We interpret 'most deals are available' to mean the maximum availability of deals, extending from the Task 1 interpretation that active means that the deal is live, not that it has a positive `qtyLeft` value
- We define the peak window to be the period of time in which the maximum number of concurrent deals avilable
- To tie break, we take the first of any such window that occurs
  i.e if a period of peak concurrent deals begins at 12:00 to 14-00 and again at 18:00-19:30 the 'peak' is considered 12:00 to:14:00

## Results

### Task 1

Deals for 3:00pm

```shell
curl '127.0.0.1:8080/api/deals?timeOfDay=15:00'
{
    "deals": [
        {
            "restaurantObjectId": "DEA567C5-F64C-3C03-FF00-E3B24909BE00",
            "restaurantName": "Masala Kitchen",
            "restaurantAddress1": "55 Walsh Street",
            "restarantSuburb": "Lower East",
            "restaurantOpen": "3:00pm",
            "restaurantClose": "9:00pm",
            "dealObjectId": "DEA567C5-0000-3C03-FF00-E3B24909BE00",
            "discount": "50",
            "dineIn": "false",
            "lightning": "true",
            "qtyLeft": "5"
        },
        {
            "restaurantObjectId": "DEA567C5-F64C-3C03-FF00-E3B24909BE00",
            "restaurantName": "Masala Kitchen",
            "restaurantAddress1": "55 Walsh Street",
            "restarantSuburb": "Lower East",
            "restaurantOpen": "3:00pm",
            "restaurantClose": "9:00pm",
            "dealObjectId": "DEA567C5-1111-3C03-FF00-E3B24909BE00",
            "discount": "40",
            "dineIn": "true",
            "lightning": "false",
            "qtyLeft": "4"
        },
        {
            "restaurantObjectId": "D80263E8-FD89-2C70-FF6B-D854ADB8DB00",
            "restaurantName": "ABC Chicken",
            "restaurantAddress1": "361 Queen Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "12:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "D80263E8-0000-2C70-FF6B-D854ADB8DB00",
            "discount": "30",
            "dineIn": "false",
            "lightning": "false",
            "qtyLeft": "1"
        },
        {
            "restaurantObjectId": "D80263E8-FD89-2C70-FF6B-D854ADB8DB00",
            "restaurantName": "ABC Chicken",
            "restaurantAddress1": "361 Queen Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "12:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "D80263E8-1111-2C70-FF6B-D854ADB8DB00",
            "discount": "20",
            "dineIn": "true",
            "lightning": "false",
            "qtyLeft": "4"
        },
        {
            "restaurantObjectId": "B5713CD0-91BF-40C7-AFC3-7D46D26B00BF",
            "restaurantName": "Kekou",
            "restaurantAddress1": "396 Bridge Road",
            "restarantSuburb": "Richmond",
            "restaurantOpen": "1:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "B5713CD0-0000-40C7-AFC3-7D46D26B00BF",
            "discount": "10",
            "dineIn": "true",
            "lightning": "true",
            "qtyLeft": "3"
        }
    ]
}
```

Deals for 6:00pm

```shell
curl '127.0.0.1:8080/api/deals?timeOfDay=18:00'
{
    "deals": [
        {
            "restaurantObjectId": "DEA567C5-F64C-3C03-FF00-E3B24909BE00",
            "restaurantName": "Masala Kitchen",
            "restaurantAddress1": "55 Walsh Street",
            "restarantSuburb": "Lower East",
            "restaurantOpen": "3:00pm",
            "restaurantClose": "9:00pm",
            "dealObjectId": "DEA567C5-0000-3C03-FF00-E3B24909BE00",
            "discount": "50",
            "dineIn": "false",
            "lightning": "true",
            "qtyLeft": "5"
        },
        {
            "restaurantObjectId": "DEA567C5-F64C-3C03-FF00-E3B24909BE00",
            "restaurantName": "Masala Kitchen",
            "restaurantAddress1": "55 Walsh Street",
            "restarantSuburb": "Lower East",
            "restaurantOpen": "3:00pm",
            "restaurantClose": "9:00pm",
            "dealObjectId": "DEA567C5-1111-3C03-FF00-E3B24909BE00",
            "discount": "40",
            "dineIn": "true",
            "lightning": "false",
            "qtyLeft": "4"
        },
        {
            "restaurantObjectId": "D80263E8-FD89-2C70-FF6B-D854ADB8DB00",
            "restaurantName": "ABC Chicken",
            "restaurantAddress1": "361 Queen Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "12:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "D80263E8-0000-2C70-FF6B-D854ADB8DB00",
            "discount": "30",
            "dineIn": "false",
            "lightning": "false",
            "qtyLeft": "1"
        },
        {
            "restaurantObjectId": "D80263E8-FD89-2C70-FF6B-D854ADB8DB00",
            "restaurantName": "ABC Chicken",
            "restaurantAddress1": "361 Queen Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "12:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "D80263E8-1111-2C70-FF6B-D854ADB8DB00",
            "discount": "20",
            "dineIn": "true",
            "lightning": "false",
            "qtyLeft": "4"
        },
        {
            "restaurantObjectId": "CDB2B42A-248C-EE20-FF45-8D0A8057E200",
            "restaurantName": "Vrindavan",
            "restaurantAddress1": "261 Harris Street",
            "restarantSuburb": "Pyrmont",
            "restaurantOpen": "6:00pm",
            "restaurantClose": "9:00pm",
            "dealObjectId": "CDB2B42A-0000-EE20-FF45-8D0A8057E200",
            "discount": "10",
            "dineIn": "true",
            "lightning": "true",
            "qtyLeft": "5"
        },
        {
            "restaurantObjectId": "B5713CD0-91BF-40C7-AFC3-7D46D26B00BF",
            "restaurantName": "Kekou",
            "restaurantAddress1": "396 Bridge Road",
            "restarantSuburb": "Richmond",
            "restaurantOpen": "1:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "B5713CD0-0000-40C7-AFC3-7D46D26B00BF",
            "discount": "10",
            "dineIn": "true",
            "lightning": "true",
            "qtyLeft": "3"
        },
        {
            "restaurantObjectId": "B5713CD0-91BF-40C7-AFC3-7D46D26B00BF",
            "restaurantName": "Kekou",
            "restaurantAddress1": "396 Bridge Road",
            "restarantSuburb": "Richmond",
            "restaurantOpen": "1:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "B5713CD0-1111-40C7-AFC3-7D46D26B00BF",
            "discount": "15",
            "dineIn": "true",
            "lightning": "true",
            "qtyLeft": "4"
        },
        {
            "restaurantObjectId": "21076F54-03E7-3115-FF09-75D07FFC7401",
            "restaurantName": "Gyoza Gyoza Melbourne Central",
            "restaurantAddress1": "211 La Trobe Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "4:00pm",
            "restaurantClose": "10:00pm",
            "dealObjectId": "B5913CD0-0000-40C7-AFC3-7D46D26B01BF",
            "discount": "25",
            "dineIn": "true",
            "lightning": "false",
            "qtyLeft": "3"
        },
        {
            "restaurantObjectId": "21076F54-03E7-3115-FF09-75D07FFC7401",
            "restaurantName": "Gyoza Gyoza Melbourne Central",
            "restaurantAddress1": "211 La Trobe Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "4:00pm",
            "restaurantClose": "10:00pm",
            "dealObjectId": "B5713CD0-1111-40C7-AFC3-7D46D26B00BF",
            "discount": "15",
            "dineIn": "false",
            "lightning": "false",
            "qtyLeft": "4"
        }
    ]
}
```

Deals for 9:00pm

```shell
curl '127.0.0.1:8080/api/deals?timeOfDay=21:00'
{
    "deals": [
        {
            "restaurantObjectId": "D80263E8-FD89-2C70-FF6B-D854ADB8DB00",
            "restaurantName": "ABC Chicken",
            "restaurantAddress1": "361 Queen Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "12:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "D80263E8-0000-2C70-FF6B-D854ADB8DB00",
            "discount": "30",
            "dineIn": "false",
            "lightning": "false",
            "qtyLeft": "1"
        },
        {
            "restaurantObjectId": "D80263E8-FD89-2C70-FF6B-D854ADB8DB00",
            "restaurantName": "ABC Chicken",
            "restaurantAddress1": "361 Queen Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "12:00pm",
            "restaurantClose": "11:00pm",
            "dealObjectId": "D80263E8-1111-2C70-FF6B-D854ADB8DB00",
            "discount": "20",
            "dineIn": "true",
            "lightning": "false",
            "qtyLeft": "4"
        },
        {
            "restaurantObjectId": "21076F54-03E7-3115-FF09-75D07FFC7401",
            "restaurantName": "Gyoza Gyoza Melbourne Central",
            "restaurantAddress1": "211 La Trobe Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "4:00pm",
            "restaurantClose": "10:00pm",
            "dealObjectId": "B5913CD0-0000-40C7-AFC3-7D46D26B01BF",
            "discount": "25",
            "dineIn": "true",
            "lightning": "false",
            "qtyLeft": "3"
        },
        {
            "restaurantObjectId": "21076F54-03E7-3115-FF09-75D07FFC7401",
            "restaurantName": "Gyoza Gyoza Melbourne Central",
            "restaurantAddress1": "211 La Trobe Street",
            "restarantSuburb": "Melbourne",
            "restaurantOpen": "4:00pm",
            "restaurantClose": "10:00pm",
            "dealObjectId": "B5713CD0-1111-40C7-AFC3-7D46D26B00BF",
            "discount": "15",
            "dineIn": "false",
            "lightning": "false",
            "qtyLeft": "4"
        }
    ]
}
```

### Task 2

```shell
curl '127.0.0.1/api/peak'
{"peakTimeStart":"6:00pm","peakTimeEnd":"9:00pm"}
```
