//package ru.kekens;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//
//public class WebServiceClient {
//    public static void main(String[] args) throws MalformedURLException {
//        URL url = new URL("http://localhost:8080/AccountService?wsdl");
//        AccountService accountService = new PersonService(url);
//        List<Person> persons =
//                personService.getPersonWebServicePort().getPersons();
//        for (Person person : persons) {
//            System.out.println("name: " + person.getName() +
//                    ", surname: " + person.getSurname() + ", age: " +
//                    person.getAge());
//        }
//        System.out.println("Total persons: " + persons.size());
//    }
//}