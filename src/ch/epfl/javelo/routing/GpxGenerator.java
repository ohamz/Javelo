package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import static java.util.Locale.ROOT;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public class that handles the GPX generation of the itineraries
 */
public class GpxGenerator {

    /**
     * Private constructor of the class GpxGenerator
     */
    private GpxGenerator(){
    }

    /**
     * Public method that creates the Gpx Document using a Route and an Elevation Profile
     * @param route : the route used
     * @param profile : the profile used
     * @return a GPX document
     */
    public static Document createGpx(Route route, ElevationProfile profile) {
        Document doc = newDocument(); // voir plus bas

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        List<PointCh> routePoints = route.points();
        Iterator<Edge> iterator = route.edges().iterator();
        double routeLength = 0;

        for (PointCh routePoint : routePoints) {
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lon", String.format(ROOT, "%.5f", Math.toDegrees(routePoint.lon())));
            rtept.setAttribute("lat", String.format(ROOT, "%.5f", Math.toDegrees(routePoint.lat())));
            rte.appendChild(rtept);

            Element ele = doc.createElement("ele");
            ele.setTextContent(String.format(ROOT, "%.2f", profile.elevationAt(routeLength)));
            rtept.appendChild(ele);

            if (iterator.hasNext()) routeLength += iterator.next().length();
        }
        return doc;
    }

    /**
     * Private method that creates the Document
     * @return a GPX Document
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

    /**
     * Public method that writes the Gpx document
     * @param name : name of the path
     * @param route : the route used
     * @param profile : the profile used
     * @throws IOException if error occurs when opening files
     */
    public static void writeGpx(String name, Route route, ElevationProfile profile) throws IOException {
        Document doc = createGpx(route, profile);
        Writer w = Files.newBufferedWriter(Path.of(name));
        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e);
        }
    }

}
