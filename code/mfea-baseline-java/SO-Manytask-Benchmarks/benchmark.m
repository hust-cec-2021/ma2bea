function Tasks = benchmark(ID)
%BENCHMARK function
    task_size = 50;
    dim = 50;
    choice_functions = [];
    switch(ID)
        case 1
            choice_functions = [1];
        case 2
            choice_functions = [2];
        case 3
            choice_functions = [4];
        case 4
            choice_functions = [1 2 3];
        case 5
            choice_functions = [4 5 6];
        case 6 
            choice_functions = [2 5 7];
        case 7
            choice_functions = [3 4 6];
        case 8
            choice_functions = [2 3 4 5 6];
        case 9
            choice_functions = [2 3 4 5 6 7];
        case 10
            choice_functions = [3 4 5 6 7];
        otherwise
            fprintf("Invalid input: ID should be in [1,10]");
    end
    for task_id = 1:task_size
        func_id = choice_functions(mod(task_id-1,length(choice_functions))+1);
        file_dir = strcat(".\Tasks\benchmark_", string(ID));
        shift_file = strcat("\bias_", string(task_id));
        rotation_file = strcat("\matrix_", string(task_id));
        matrix = load(strcat(file_dir, rotation_file));
        shift = load(strcat(file_dir, shift_file));
        switch func_id
            case 1
                Tasks(task_id).dim = dim;            % dimensionality of Task
                Tasks(task_id).Lb = -100*ones(1,dim); % Lower bound of Task
                Tasks(task_id).Ub = 100*ones(1,dim);  % Upper bound of Task
                auxiliary(task_id).shift = shift.*ones(1,50);
                auxiliary(task_id).matrix = matrix;
                Tasks(task_id).fnc = @(x)Sphere(x,auxiliary(task_id).matrix,auxiliary(task_id).shift); % function of Task
               
            case 2
                Tasks(task_id).dim = dim;
                Tasks(task_id).Lb = -50*ones(1,dim);
                Tasks(task_id).Ub = 50*ones(1,dim); 
                auxiliary(task_id).shift = shift.*ones(1,50);
                auxiliary(task_id).matrix = matrix;
                Tasks(task_id).fnc = @(x)Rosenbrock(x,auxiliary(task_id).matrix,auxiliary(task_id).shift);
            case 3
                Tasks(task_id).dim = dim;
                Tasks(task_id).Lb = -50*ones(1,dim);
                Tasks(task_id).Ub = 50*ones(1,dim);
                auxiliary(task_id).shift = shift.*ones(1,50);
                auxiliary(task_id).matrix = matrix;
                Tasks(task_id).fnc = @(x)Ackley(x,auxiliary(task_id).matrix,auxiliary(task_id).shift);
            case 4
                Tasks(task_id).dim = dim;
                Tasks(task_id).Lb = -50*ones(1,dim);
                Tasks(task_id).Ub = 50*ones(1,dim);
                auxiliary(task_id).shift = shift.*ones(1,50);
                auxiliary(task_id).matrix = matrix;
                Tasks(task_id).fnc = @(x)Rastrigin(x,auxiliary(task_id).matrix,auxiliary(task_id).shift);
            case 5
                Tasks(task_id).dim = dim;
                Tasks(task_id).Lb = -100*ones(1,dim);
                Tasks(task_id).Ub = 100*ones(1,dim);
                auxiliary(task_id).shift = shift.*ones(1,50);
                auxiliary(task_id).matrix = matrix;
                Tasks(task_id).fnc = @(x)Griewank(x,auxiliary(task_id).matrix,auxiliary(task_id).shift);
            case 6
                Tasks(task_id).dim = dim;
                Tasks(task_id).Lb = -0.5*ones(1,dim);
                Tasks(task_id).Ub = 0.5*ones(1,dim);
                auxiliary(task_id).shift = shift.*ones(1,50);
                auxiliary(task_id).matrix = matrix;
                Tasks(task_id).fnc = @(x)Weierstrass(x,auxiliary(task_id).matrix,auxiliary(task_id).shift);
            case 7
                Tasks(task_id).dim = dim;
                Tasks(task_id).Lb = -500*ones(1,dim);
                Tasks(task_id).Ub = 500*ones(1,dim);
                auxiliary(task_id).shift = shift.*ones(1,50);
                auxiliary(task_id).matrix = matrix;
                Tasks(task_id).fnc = @(x)Schwefel(x,auxiliary(task_id).matrix,auxiliary(task_id).shift);
        end
end