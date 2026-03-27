mvn clean install;

# Run the stack profiler.
java -jar .\target\benchmarks.jar `
	ProfilingDeepSearch.* `
	-prof stack;

java -jar .\target\benchmarks.jar `
	ProfilingShallowSearch.* `
	-prof stack;

# # Run the GC profiler
# java -jar .\target\benchmarks.jar `
# 	SearchProfiling.AB `
# 	-prof gc;
