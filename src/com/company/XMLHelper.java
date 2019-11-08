package com.company;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class XMLHelper {
    private static final String XML_PATH = "src/katalog.xml";
    private static final String[] LABELS = {"manufacturer", "size", "resolution", "type", "touchscreen", "name",
            "physical_cores", "clock_speed", "ram", "storage", "type", "name", "memory", "os", "disc_reader"};
    private DocumentBuilderFactory documentFactory;
    private DocumentBuilder documentBuilder;
    private Document document;

    public XMLHelper() throws ParserConfigurationException {
        documentFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentFactory.newDocumentBuilder();
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    public Vector<Vector<String>> readXML() {
        Vector<Vector<String>> records = new Vector<>();
        try {
            File file = new File(XML_PATH);
            file.createNewFile();
            document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();

            NodeList laptops = document.getElementsByTagName("laptop");
            for (int i = 0; i < laptops.getLength(); i++) {
                Vector<String> record = new Vector<>();
                Node laptop = laptops.item(i);
                if (laptop.getNodeType() == Node.ELEMENT_NODE) {
                    record=getNodeValues(laptop.getChildNodes());
                }
                records.add(record);
            }

        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return records;
    }


    private Vector<String> getNodeValues(NodeList nodeList) {
        Vector<String> values = new Vector<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                int nodeChildsNumber = node.getChildNodes().getLength();
                if(nodeChildsNumber < 2 ){
                    values.add(node.getTextContent());
                }
                else {
                    values.addAll(getNodeValues(node.getChildNodes()));
                }
            }
        }
        return values;
    }

    public void createXML(Vector<Vector<String>> records) {
        document = documentBuilder.newDocument();
        try {
            Element laptops = document.createElement("laptops");
            Attr moddate = document.createAttribute("moddate");
            moddate.setValue(getCurrentTimestamp());
            laptops.setAttributeNode(moddate);
            document.appendChild(laptops);

            int i = 1;
            for (Vector<String> record : records) {
                createLaptopElement(laptops, record, i);
                i++;
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(XML_PATH));

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");
        } catch (TransformerException pce) {
            pce.printStackTrace();
        }
    }

    private void createLaptopElement(Element parent, Vector<String> record, int id) {
        //laptop
        Element laptop = document.createElement("laptop");
        parent.appendChild(laptop);
        laptop.setAttribute("id", Integer.toString(id));

        //manufacturer
        Element manufacturer = document.createElement("manufacturer");
        manufacturer.appendChild(document.createTextNode(record.get(0)));
        laptop.appendChild(manufacturer);

        //screen
        Element screen = document.createElement("screen");
        laptop.appendChild(screen);
        //size
        Element size = document.createElement("size");
        size.appendChild(document.createTextNode(record.get(1)));
        screen.appendChild(size);
        //resolution
        Element resolution = document.createElement("resolution");
        resolution.appendChild(document.createTextNode(record.get(2)));
        screen.appendChild(resolution);
        //screenType
        Element screenType = document.createElement("type");
        screenType.appendChild(document.createTextNode(record.get(3)));
        screen.appendChild(screenType);
        //touchscreen
        Element touchscreen = document.createElement("touchscreen");
        touchscreen.appendChild(document.createTextNode(record.get(4)));
        screen.appendChild(touchscreen);

        //processor
        Element processor = document.createElement("processor");
        laptop.appendChild(processor);
        //processorName
        Element processorName = document.createElement("name");
        processorName.appendChild(document.createTextNode(record.get(5)));
        processor.appendChild(processorName);
        //physical_cores
        Element physicalCores = document.createElement("physical_cores");
        physicalCores.appendChild(document.createTextNode(record.get(6)));
        processor.appendChild(physicalCores);
        //clock_speed
        Element clockSpeed = document.createElement("clock_speed");
        clockSpeed.appendChild(document.createTextNode(record.get(7)));
        processor.appendChild(clockSpeed);

        //ram
        Element ram = document.createElement("ram");
        ram.appendChild(document.createTextNode(record.get(8)));
        laptop.appendChild(ram);

        //disc
        Element disc = document.createElement("disc");
        laptop.appendChild(disc);
        //storage
        Element storage = document.createElement("storage");
        storage.appendChild(document.createTextNode(record.get(9)));
        disc.appendChild(storage);
        //type
        Element discType = document.createElement("disc");
        discType.appendChild(document.createTextNode(record.get(10)));
        disc.appendChild(discType);

        //graphic_card
        Element graphicCard = document.createElement("graphic_card");
        laptop.appendChild(graphicCard);
        //graphicName
        Element graphicName = document.createElement("name");
        graphicName.appendChild(document.createTextNode(record.get(11)));
        graphicCard.appendChild(graphicName);
        //memory
        Element memory = document.createElement("memory");
        memory.appendChild(document.createTextNode(record.get(12)));
        graphicCard.appendChild(memory);

        //os
        Element os = document.createElement("os");
        os.appendChild(document.createTextNode(record.get(13)));
        laptop.appendChild(os);

        //disc_reader
        Element disc_reader = document.createElement("disc_reader");
        disc_reader.appendChild(document.createTextNode(record.get(14)));
        laptop.appendChild(disc_reader);

    }
}
