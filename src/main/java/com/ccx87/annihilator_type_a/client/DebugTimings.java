package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.AnnihilatorTypeA;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public final class DebugTimings {
   private static final Map<String, long[]> SAMPLES = new LinkedHashMap<>();
   private static final long WINDOW_NANOS = 1000000000L;
   private static long windowStart = System.nanoTime();

   private DebugTimings() {
   }

   public static long begin() {
      return 0L;
   }

   public static void end(String section, long startedAt) {
      if (startedAt != 0L) {
         long[] slot = SAMPLES.computeIfAbsent(section, key -> new long[2]);
         slot[0] += System.nanoTime() - startedAt;
         slot[1]++;
         long now = System.nanoTime();
         if (now - windowStart >= 1000000000L) {
            report(now);
         }
      }
   }

   private static void report(long now) {
      double seconds = (double)(now - windowStart) / 1.0E9;
      StringBuilder line = new StringBuilder();
      line.append(String.format(Locale.ROOT, "[timings] window %.1fs", seconds));

      for (Entry<String, long[]> entry : SAMPLES.entrySet()) {
         long[] slot = entry.getValue();
         if (slot[1] != 0L) {
            line.append(String.format(Locale.ROOT, "  %s=%.3fms(x%d)", entry.getKey(), (double)slot[0] / 1000000.0 / (double)slot[1], slot[1]));
         }
      }

      AnnihilatorTypeA.LOGGER.info(line.toString());
      SAMPLES.clear();
      windowStart = now;
   }
}
