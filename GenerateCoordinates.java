import java.util.HashMap;

public class GenerateCoordinates {

    // getiing all regions
    public static HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> getCoordinatesFromCoverImg(
            int[] imgCoordinates) {

        // region no -> segment no -> start/end -> x/y coordinates
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> regions = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>>();

        int xs = imgCoordinates[0];
        int ys = imgCoordinates[1];
        int xe = imgCoordinates[2];
        int ye = imgCoordinates[3];

        regions.put("region-1", getRegionCoordinates(new int[] { xs, ys, xe / 2, ye / 2 }));
        regions.put("region-2", getRegionCoordinates(new int[] { xe / 2 + 1, ys, xe, ye / 2 }));
        regions.put("region-3", getRegionCoordinates(new int[] { xs, ye / 2 + 1, xe / 2, ye }));
        regions.put("region-4", getRegionCoordinates(new int[] { xe / 2 + 1, ye / 2 + 1, xe, ye }));

        return regions;
    }

    // getting all segments
    private static HashMap<String, HashMap<String, HashMap<String, Integer>>> getRegionCoordinates(
            int[] regionCoordinates) {
        HashMap<String, HashMap<String, HashMap<String, Integer>>> region = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();

        int xs = regionCoordinates[0];
        int ys = regionCoordinates[1];
        int xe = regionCoordinates[2];
        int ye = regionCoordinates[3];

        region.put("segment-1", getSegmentCoordinates(new int[] { xs, ys, xe, ys+(ye-ys)/4 }));
        region.put("segment-2", getSegmentCoordinates(new int[] { xs, ys+(ye-ys)/4 + 1, xe, ys+(ye-ys)/2 }));
        region.put("segment-3", getSegmentCoordinates(new int[] { xs, ys+(ye-ys)/2 + 1, xe, ys+((ye-ys) * 3)/4 }));
        region.put("segment-4", getSegmentCoordinates(new int[] { xs, ys+((ye-ys) * 3)/4 + 1, xe, ye }));

        return region;
    }

    // getting start and end coordinates
    private static HashMap<String, HashMap<String, Integer>> getSegmentCoordinates(int[] segmentCoordinates) {

        HashMap<String, HashMap<String, Integer>> segment = new HashMap<String, HashMap<String, Integer>>();

        int xs = segmentCoordinates[0];
        int ys = segmentCoordinates[1];
        int xe = segmentCoordinates[2];
        int ye = segmentCoordinates[3];

        HashMap<String, Integer> start = new HashMap<String, Integer>();
        start.put("x", xs);
        start.put("y", ys);

        HashMap<String, Integer> end = new HashMap<String, Integer>();
        end.put("x", xe);
        end.put("y", ye);

        segment.put("start", start);
        segment.put("end", end);

        return segment;
    }
}
